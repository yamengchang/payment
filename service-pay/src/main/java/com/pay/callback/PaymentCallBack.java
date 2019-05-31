package com.pay.callback;

import com.alibaba.fastjson.JSONObject;
import com.omv.common.util.basic.JSONUtil;
import com.omv.common.util.basic.MapUtil;
import com.omv.common.util.date.DateUtil;
import com.omv.common.util.httpclient.bean.HttpBean;
import com.omv.common.util.httpclient.bean.RequestMethod;
import com.omv.common.util.treadPool.ThreadPoolUtils;
import com.omv.redis.config.jedis.RedisClient;
import com.pay.bean.Constants;
import com.pay.bean.PaymentResult;
import com.pay.dao.ConvenientPaymentRepository;
import com.pay.entity.ConvenientPayment;
import com.pay.entity.LeaseSerialRecord;
import com.pay.entity.PaymentInfo;
import com.pay.service.LeaseSerialRecordService;
import com.pay.service.PaymentInfoService;
import com.pay.thread.HttpThread;
import com.pay.utils.union.sdk.AcpService;
import com.pay.utils.union.sdk.LogUtil;
import com.pay.utils.union.sdk.SDKConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zwj on 2018/10/15.
 * 支付回调
 */
@RestController
public class PaymentCallBack {
    @Autowired
    private PaymentInfoService paymentInfoService;
    @Autowired
    private LeaseSerialRecordService serialRecordService;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private ConvenientPaymentRepository convenientPaymentRepository;
    /*
    * ============== Start 兴业聚合支付回调
    * */
    @RequestMapping("/callback/xy/wechat")
    public String wechatCallBack(@RequestParam Map<String, Object> callbackParams) throws IOException, ParseException {
        System.out.println("=============== XY 微信支付回调 参数 ===============  ");
        System.out.println(callbackParams.toString());
        return xyPolymericTrade(MapUtil.str2Map((String) callbackParams.get("bodyContent")),"wechat").toString();
    }

    @RequestMapping("/callback/xy/alipay")
    public String alipayCallBack(@RequestParam Map<String, Object> callbackParams) throws IOException, ParseException {
        System.out.println("=============== XY 支付宝支付回调 参数 ===============  ");
        System.out.println(callbackParams.toString());
        return xyPolymericTrade(MapUtil.str2Map((String) callbackParams.get("bodyContent")),"alipay").toString();
    }

    @RequestMapping("/callback/xy/refund")
    public String xyRefundCallBack(@RequestParam Map<String, Object> callbackParams) throws IOException, ParseException {
        System.out.println("=============== XY 退款回调 =============== ");
        System.out.println(callbackParams.toString());
//        return xyPolymericTrade(callbackParams).toString();
        return null;
    }
    /*
    * ============== End 兴业聚合支付回调
    * */



    /*
    * ============== Start 银联聚合支付回调
    * */
    @RequestMapping("/callback/union/wechat")
    public String wechatCallBackUnion(@RequestParam Map<String, Object> callbackParams) throws ParseException {
        System.out.println("Union 微信支付回调：" + callbackParams.toString());
        return unionPolymericTrade(callbackParams,"wechat").toString();
    }

    @RequestMapping("/callback/union/alipay")
    public String alipayCallBackUnion(@RequestParam Map<String, Object> callbackParams) throws ParseException {
        System.out.println("Union 支付宝支付回调：" + callbackParams.toString());
        return unionPolymericTrade(callbackParams, "alipay").toString();
    }

    @RequestMapping("/callback/union/apl")
    public String aplCallBackUnion(@RequestParam Map<String, Object> callbackParams) throws ParseException {
        System.out.println("Union Apple pay 支付回调：" + callbackParams.toString());
        return unionPolymericTrade(callbackParams, "apple").toString();
    }



    /*
    * ============== End 银联聚合支付回调
    * */

    @RequestMapping("/callback/union/ylpay")
    public void ylpayCallBackUnion(Map<String, Object> callbackParams, HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("Union 银联便民缴费支付回调：" + callbackParams.toString());
        convenientCallBack(request, response);
    }

    private void convenientCallBack(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LogUtil.writeLog("BackRcvResponse接收后台通知开始");
        String encoding = req.getParameter(SDKConstants.param_encoding);
        // 获取银联通知服务器发送的后台通知参数
        Map<String, String> reqParam = getAllRequestParam(req);
        LogUtil.printRequestLog(reqParam);
        Map<String, String> valideData = null;
        if (!reqParam.isEmpty()) {
            Iterator<Map.Entry<String, String>> it = reqParam.entrySet().iterator();
            valideData = new HashMap<String, String>(reqParam.size());
            while (it.hasNext()) {
                Map.Entry<String, String> e = it.next();
                String key = (String) e.getKey();
                String value = (String) e.getValue();
                valideData.put(key, value);
            }
        }
        //重要！验证签名前不要修改reqParam中的键值对的内容，否则会验签不过
        if (!AcpService.validate(valideData, encoding)) {
            LogUtil.writeLog("验证签名结果[失败].");
            //验签失败，需解决验签问题
        } else {
            LogUtil.writeLog("验证签名结果[成功].");
            //【注：为了安全验签成功才应该写商户的成功处理逻辑】交易成功，更新商户订单状态
            String orderId = valideData.get("orderId"); //获取后台通知的数据，其他字段也可用类似方式获取
            String respCode = valideData.get("respCode");
            //TODO 只判断成功的orderId，不判断重复
            ConvenientPayment convenientPayment = convenientPaymentRepository.findById(orderId).orElse(null);
            if (convenientPayment==null){
                return;
            }
            convenientPayment.setBackUrl("0");
            convenientPayment.setReturnMsg(respCode);
            convenientPayment.setResult("0");
            convenientPaymentRepository.save(convenientPayment);
            ThreadPoolUtils.addFixedThread(new HttpThread(convenientPayment.getBackUrl(), reqParam));
            resp.getWriter().print("ok");
        }
        LogUtil.writeLog("BackRcvResponse接收后台通知结束");
    }

    private Map<String, String> getAllRequestParam(HttpServletRequest request) {
        Map<String, String> resultMap = new HashMap<>();
        Map<String, String[]> tempMap = request.getParameterMap();
        Set<String> keys = tempMap.keySet();
        for (String key : keys) {
            String modelName = new String(request.getParameter(key));
            resultMap.put(key, modelName);
        }
        return resultMap;
    }
    /*
    *@Author : Gavin
    *@Email : gavinsjq@sina.com
    *@Date: 2018/11/28 15:36
    *@Description :
    *@Params :  * @param callbackParams 银联支付回调参数
    *              @param channel 支付方式 wechat   alipay apple
    */
    private String unionPolymericTrade(Map<String, Object> callbackParams, String channel) throws ParseException {
        String result_code = (String) callbackParams.get("respCode");
        String transaction_no = (String) callbackParams.get("payId");
        String orderNo = (String) callbackParams.get("orderNo");
        String time_paid = (String) callbackParams.get("payTime");

        LeaseSerialRecord serialRecord = serialRecordService.findByOrderNo(orderNo);
        PaymentInfo paymentInfo = paymentInfoService.findByInstanceId(serialRecord.getInstanceId());
        String callbackUrl = paymentInfo.getWxCallBackUrl();
        if (!serialRecord.getStatus().equals(Constants.PAYMENT_STATUS_SUCCESS)) {
            if (result_code.equals("0000")) {
                updateSerialRecord(serialRecord, transaction_no, Constants.PAYMENT_STATUS_SUCCESS, time_paid);
            } else {
                updateSerialRecord(serialRecord, transaction_no, Constants.PAYMENT_STATUS_FAILED, time_paid);
            }
        }
        boolean flag = sendToInstance(callbackUrl, "union", callbackParams, serialRecord,channel);
        if (flag) {
            return "SUCCESS";
        }
        return null;
    }

    public Object xyPolymericTrade(Map<String, Object> callbackParams,String channel) throws ParseException {
        String result_code = (String) callbackParams.get("result_code");
        String transaction_no = (String) callbackParams.get("transaction_no");
        String orderNo = (String) callbackParams.get("order_no");
        String time_paid = (String) callbackParams.get("time_paid");
        LeaseSerialRecord serialRecord = serialRecordService.findByOrderNo(orderNo);
        PaymentInfo paymentInfo = paymentInfoService.findByInstanceId(serialRecord.getInstanceId());
        String callbackUrl = paymentInfo.getWxCallBackUrl();
        if (!serialRecord.getStatus().equals(Constants.PAYMENT_STATUS_SUCCESS)) {
            if (result_code.equals("0")) {
                updateSerialRecord(serialRecord, transaction_no, Constants.PAYMENT_STATUS_SUCCESS, time_paid);
            } else {
                updateSerialRecord(serialRecord, transaction_no, Constants.PAYMENT_STATUS_FAILED, time_paid);
            }
        }

        boolean flag = sendToInstance(callbackUrl, "xy", callbackParams, serialRecord, channel);
        if (flag) {
            JSONObject response = new JSONObject();
            response.put("return_code", "SUCCESS");
            return response.toJSONString();
        }
        return null;
    }

    public Boolean sendToInstance(String url, String paymentChannel, Map<String, Object> callbackParams, LeaseSerialRecord serialRecord, String channel) {
        String mrch_id = null, result_code = null, status = null, time_paid = null, transaction_no = null;
        Double amount = null, amount_refunded = null, amount_settle = null;
        if (paymentChannel.equals("xy")) {
            mrch_id = (String) callbackParams.get("mrch_id");
            result_code = (String) callbackParams.get("result_code");
            status = (String) callbackParams.get("status");
            time_paid = (String) callbackParams.get("time_paid");
            amount = (Double) callbackParams.get("amount");
            transaction_no = (String) callbackParams.get("transaction_no");
        }else if(paymentChannel.equals("union")){
            mrch_id = (String) callbackParams.get("merNo");
            result_code = (String) callbackParams.get("respCode");
            if(result_code.equals("0000")){
                status = "SUCCESS";
            }else{
                status = "FAILED";
            }
            time_paid = (String) callbackParams.get("payTime");
            amount = Double.valueOf(String.valueOf(callbackParams.get("transAmt")));
            transaction_no = (String) callbackParams.get("payId");
        }

        PaymentResult result = new PaymentResult();
        result.setStatus(status);
        result.setAmount(String.valueOf(amount / 100));
        result.setMrch_id(mrch_id);
        result.setTime_paid(time_paid);
        result.setOrder_no(serialRecord.getOriOrderNo());
        result.setPay_order_no(serialRecord.getOrderNo());
        redisClient.set(serialRecord.getInstanceId() + serialRecord.getOriOrderNo(), JSONUtil.objectToJsonStr(result), 3600);

        HttpBean httpBean = new HttpBean(url, RequestMethod.post);
        httpBean.addParameter("mrch_id", mrch_id);
        httpBean.addParameter("orderNo", serialRecord.getOriOrderNo());
        httpBean.addParameter("status", status);
        httpBean.addParameter("time_paid", time_paid);
        httpBean.addParameter("amount", amount);
        httpBean.addParameter("transaction_no", transaction_no);
        httpBean.addParameter("channel", channel);
        httpBean.run();
        String response = httpBean.getResponseContent();

        if (response.equals("SUCCESS")) {
            return true;
        } else {
            return false;
        }

    }

    private void updateSerialRecord(LeaseSerialRecord serialRecord, String serialNo, String paymentStatus, String transOverTime) throws ParseException {
        serialRecord.setSerialNo(serialNo);
        serialRecord.setStatus(paymentStatus);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = format.parse(transOverTime);
        serialRecord.setTransOverTime(DateUtil.toString(date, "yyyy-MM-dd HH:mm:ss"));
        serialRecordService.save(serialRecord);

    }


    private boolean checkSerialRecordExist(String serialNo) {
        LeaseSerialRecord serialRecord = serialRecordService.findBySerialNo(serialNo);
        if (null != serialRecord) {//流水已存在
            return true;
        } else {
            return false;
        }
    }

}
