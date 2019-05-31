package com.pay.controller.life;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.omv.common.util.basic.HttpServletUtil;
import com.omv.common.util.basic.JSONUtil;
import com.omv.common.util.basic.ValueUtil;
import com.omv.common.util.error.CustomException;
import com.omv.common.util.signature.EncryptManager;
import com.pay.bean.Channel;
import com.pay.bean.Constants;
import com.pay.bean.InstanceTypeEnum;
import com.pay.bean.PayFactory;
import com.pay.entity.Instance;
import com.pay.remote.SupervisionFeign;
import com.pay.service.InstanceService;
import com.pay.service.PayService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

/**
 * Created by zwj on 2018/9/26.
 * 支付控制层
 */
@Controller
public class PayController {

    @Autowired
    private Constants constants;
    @Autowired
    private PayFactory payFactory;
    @Autowired
    private InstanceService instanceService;
    @Autowired
    private SupervisionFeign supervisionFeign;

//    https://gateway.95516.com/jiaofei/config/s/areas  获取地区列表
//    https://gateway.95516.com/jiaofei/config/a/biz/31/1   根据地区和业务类型获取业务要素
//    https://gateway.95516.com/jiaofei/config/s/biz/D1_3300_0000   根据业务代码获取业务要素
//    https://gateway.95516.com/jiaofei/config/a/areas/1    根据业务类型获取地区
//    https://gateway.95516.com/jiaofei/config/a/categories/1401/1  根据地区和业务类型获取业务代码


//    https://gateway.95516.com/jiaofei/config/s/areas  获取地区列表
//    https://gateway.95516.com/jiaofei/config/a/categories/1401/1  根据地区和业务类型获取业务代码
//    https://gateway.95516.com/jiaofei/config/s/biz/D1_3300_0000   根据业务代码获取账单（业务）要素

//    https://gateway.95516.com/jiaofei/config/a/areas/1    根据业务类型获取地区

//    业务类型	代码
//    水费缴纳	1
//    电费缴纳	2
//    燃气费缴纳	3
//    移动缴费	4
//    联通缴费	5
//    电信缴费	6
//    铁通缴费	7
//    网通缴费	8
//    卫通缴费	9
//    移动手机充值	10
//    移动手机缴费	11
//    联通手机充值	12
//    联通手机缴费	13
//    电信手机充值	14
//    电信手机缴费	15
//    暖气费缴费	16
//    数字电视充值	17
//    数字电视缴费	18
//    有线电视缴费	19
//    税费	20
//    保险费缴纳	21
//    慈善捐款	22
//    会员费缴纳	23
//    学费缴纳	24
//    交通拯救费	25
//    交通罚款	26
//    交通规费	27
//    物业公司管理费	28
//    基金购买	29
//    机票购买	30
//    信用卡还款	31
//    Q币充值	32
//    IC卡充值	33
//    彩票投注	34
//    联合事业缴费	35
//    考试类缴费	36
//    小额汇款	37
//    资金归集	38
//    ETC缴费	39
//    还款易	40
//    公路售票	41
//    非税缴费	42
//    手机充值	43
//    固话宽带	44
//    手机流量购买	45
//    火车票购买	46
//    教育缴费	47
//    社保缴费	48
//    工会缴费	49
//    农信贷款	50
//    费用代收	51
//    加油卡充值	52
//    汽车租赁	53
//    条码查询	99

    /**
     * 便民账单查看
     *
     * @param params instanceId(id) bussCode(业务代码) billQueryInfo(附加信息:usr_num)
     * @return json
     * @throws IOException ex
     */
    @PostMapping(value = "/scrt/convenient/jf/bill", produces = "application/json")
    @ResponseBody
    public String convenientJfBill(@RequestParam Map<String, Object> params) throws IOException {
        //房协验证...验证开始...
        if(constants.getHouseVerifyEnable()){
            ValueUtil.verifyParams("roomId,instanceId", params.get("roomId"), params.get("instanceId"));
            String roomId = params.get("roomId") != null ? params.get("roomId").toString() : null;
            String res = supervisionFeign.validateConvenientJfBill(roomId);
            if (res == null) {
                ValueUtil.isError("数据不存在");
            }
            if (!"".equals(ValueUtil.getFromJson(res, "data"))) {
                ValueUtil.isError(ValueUtil.getFromJson(res, "data", "content"));
            }
        }
        //...验证结束...
        Instance instance = instanceService.findByInstanceIdAndType((String) params.get("instanceId"), InstanceTypeEnum.CONVENIENT.getCode());
//        Instance instance = instanceService.findByInstanceId((String) params.get("instanceId"));
        if (instance == null) {
            ValueUtil.isError("功能尚未开通");
        }
        Channel channel = Channel.getChannel(instance.getChannel());
        params.put("instance", instance);
        PayService payService = payFactory.getInstance(channel);
        Object obj = payService.convenientJfBill(params);
        return ValueUtil.toJson(obj);
    }

    /**
     * 便民账单查看
     *
     * @param params instanceId(id) bussCode(业务代码) billQueryInfo(附加信息:usr_num)
     * @return json
     * @throws IOException ex
     */
    @PostMapping(value = "/scrt/convenient/jf/bill/batch", produces = "application/json")
    @ResponseBody
    public String convenientJfBillBatch(@RequestParam Map<String, Object> params) throws IOException {
        //房协验证...验证开始...
        if(constants.getHouseVerifyEnable()){
            ValueUtil.verifyParams("roomId,instanceId", params.get("roomId"), params.get("instanceId"));
            String roomId = params.get("roomId") != null ? params.get("roomId").toString() : null;
            String res = supervisionFeign.validateConvenientJfBill(roomId);
            if (res == null) {
                ValueUtil.isError("数据不存在");
            }
            if (!"".equals(ValueUtil.getFromJson(res, "data"))) {
                ValueUtil.isError(ValueUtil.getFromJson(res, "data", "content"));
            }
        }

        //...验证结束...
        Instance instance = instanceService.findByInstanceIdAndType((String) params.get("instanceId"), InstanceTypeEnum.CONVENIENT.getCode());
//        Instance instance = instanceService.findByInstanceId((String) params.get("instanceId"));
        if (instance == null) {
            ValueUtil.isError("功能尚未开通");
        }
        Channel channel = Channel.getChannel(instance.getChannel());
        params.put("instance", instance);
        PayService payService = payFactory.getInstance(channel);
        Object obj = payService.convenientJfBillBatch(params);
        return ValueUtil.toJson(obj);
    }

    /**
     * 便民缴费(便民账单支付)
     *
     * @param params instanceId channel(back/app/gate) txnAmt(金额) bussCode(业务代码) billQueryInfo(附加信息:usr_num)
     * @return json
     * @throws IOException ex
     */
    @PostMapping(value = "/scrt/convenient/jf", produces = "application/json")
    @ResponseBody
    public String convenientPayment(@RequestParam Map<String, Object> params) throws IOException {
        //房协验证...验证开始...
        if(constants.getHouseVerifyEnable()){
            ValueUtil.verifyParams("roomId,leaseId", params.get("roomId"), params.get("leaseId"));
            String roomId = params.get("roomId") != null ? params.get("roomId").toString() : null;
            String leaseId = params.get("leaseId") != null ? params.get("leaseId").toString() : null;
            String res = supervisionFeign.validateConvenientPayment(roomId, leaseId);
            if (res == null) {
                ValueUtil.isError("数据不存在");
            }
            if (!"".equals(ValueUtil.getFromJson(res, "data"))) {
                ValueUtil.isError(ValueUtil.getFromJson(res, "data", "content"));
            }
        }
        //...验证结束...
        Instance instance = instanceService.findByInstanceIdAndType((String) params.get("instanceId"), InstanceTypeEnum.CONVENIENT.getCode());
//        Instance instance = instanceService.findByInstanceId((String) params.get("instanceId"));
        if (instance == null) {
            ValueUtil.isError("功能尚未开通");
        }
        Channel channel = Channel.getChannel(instance.getChannel());
        params.put("instance", instance);
        PayService payService = payFactory.getInstance(channel);
        Object obj = payService.convenientPayment(params);
        return ValueUtil.toJson(obj);
    }

    /**
     * 便民缴费(便民账单支付)
     *
     * @param file instanceId channel(back/app/gate) txnAmt(金额) bussCode(业务代码) billQueryInfo(附加信息:usr_num)
     * @return json
     * @throws IOException ex
     */
    @PostMapping(value = "/scrt/convenient/jf/batch", produces = "application/json")
    @ResponseBody
    public String convenientPaymentBatch(@RequestParam MultipartFile file) throws IOException {
        String name = "E:/var" + "/" + System.currentTimeMillis() + file.getOriginalFilename();
        file.transferTo(new File(name));
        StringBuilder result = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(name));//构造一个BufferedReader类来读取文件
        String s = null;
        while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
            result.append(System.lineSeparator()).append(s);
        }
        br.close();
        Map map = new Gson().fromJson(result.toString(),Map.class);
        //房协验证...验证开始...
        if(constants.getHouseVerifyEnable()){
            ValueUtil.verifyParams("roomId,leaseId", map.get("roomId"), map.get("leaseId"));
            String roomId = map.get("roomId") != null ? map.get("roomId").toString() : null;
            String leaseId = map.get("leaseId") != null ? map.get("leaseId").toString() : null;
            String res = supervisionFeign.validateConvenientPayment(roomId, leaseId);
            if (res == null) {
                ValueUtil.isError("数据不存在");
            }
            if (!"".equals(ValueUtil.getFromJson(res, "data"))) {
                ValueUtil.isError(ValueUtil.getFromJson(res, "data", "content"));
            }
        }

        //...验证结束...
        Instance instance = instanceService.findByInstanceIdAndType((String) map.get("instanceId"), InstanceTypeEnum.CONVENIENT.getCode());
//        Instance instance = instanceService.findByInstanceId((String) map.get("instanceId"));
        if (instance == null) {
            ValueUtil.isError("功能尚未开通");
        }
        Channel channel = Channel.getChannel(instance.getChannel());
        map.put("instance", instance);
        PayService payService = payFactory.getInstance(channel);
        Object obj = payService.convenientPaymentBatch(map);
        return ValueUtil.toJson(obj);
    }

    /**
     * 便民缴费(便民账单支付状态查询)
     *
     * @param params instanceId channel(back/app/gate) txnAmt(金额) bussCode(业务代码) billQueryInfo(附加信息:usr_num)
     * @return json
     * @throws IOException ex
     */
    @PostMapping(value = "/scrt/convenient/cx", produces = "application/json")
    @ResponseBody
    public String convenientSearch(@RequestParam Map<String, Object> params) throws IOException {
        //房协验证...验证开始...
        if(constants.getHouseVerifyEnable()){
            ValueUtil.verifyParams("roomId,leaseId", params.get("roomId"), params.get("leaseId"));
            String roomId = params.get("roomId") != null ? params.get("roomId").toString() : null;
            String leaseId = params.get("leaseId") != null ? params.get("leaseId").toString() : null;
            String res = supervisionFeign.validateConvenientPayment(roomId, leaseId);
            if (res == null) {
                ValueUtil.isError("数据不存在");
            }
            if (!"".equals(ValueUtil.getFromJson(res, "data"))) {
                ValueUtil.isError(ValueUtil.getFromJson(res, "data", "content"));
            }

        }
        //...验证结束...
        Instance instance = instanceService.findByInstanceIdAndType((String) params.get("instanceId"), InstanceTypeEnum.CONVENIENT.getCode());
//        Instance instance = instanceService.findByInstanceId((String) params.get("instanceId"));
        if (instance == null) {
            ValueUtil.isError("功能尚未开通");
        }
        Channel channel = Channel.getChannel(instance.getChannel());
        params.put("instance", instance);
        PayService payService = payFactory.getInstance(channel);
        Object obj = payService.convenientSearch(params);
        return ValueUtil.toJson(obj);
    }

    /**
     * 便民缴费(便民账单支付状态查询批量)
     *
     * @param params instanceId channel(back/app/gate) txnAmt(金额) bussCode(业务代码) billQueryInfo(附加信息:usr_num)
     * @return json
     * @throws IOException ex
     */
    @PostMapping(value = "/scrt/convenient/cx/batch", produces = "application/json")
    @ResponseBody
    public String convenientSearchBatch(@RequestParam Map<String, Object> params) throws IOException {
        //房协验证...验证开始...
        if(constants.getHouseVerifyEnable()){

            ValueUtil.verifyParams("roomId,leaseId", params.get("roomId"), params.get("leaseId"));
            String roomId = params.get("roomId") != null ? params.get("roomId").toString() : null;
            String leaseId = params.get("leaseId") != null ? params.get("leaseId").toString() : null;
            String res = supervisionFeign.validateConvenientPayment(roomId, leaseId);
            if (res == null) {
                ValueUtil.isError("数据不存在");
            }
            if (!"".equals(ValueUtil.getFromJson(res, "data"))) {
                ValueUtil.isError(ValueUtil.getFromJson(res, "data", "content"));
            }
        }
        //...验证结束...
        Instance instance = instanceService.findByInstanceIdAndType((String) params.get("instanceId"), InstanceTypeEnum.CONVENIENT.getCode());
//        Instance instance = instanceService.findByInstanceId((String) params.get("instanceId"));
        if (instance == null) {
            ValueUtil.isError("功能尚未开通");
        }
        Channel channel = Channel.getChannel(instance.getChannel());
        params.put("instance", instance);
        PayService payService = payFactory.getInstance(channel);
        Object obj = payService.convenientSearchBatch(params);
        return ValueUtil.toJson(obj);
    }

    /*
     * 聚合支付
     * */
    @RequestMapping("/scrt/polymeric/trade")
    public ModelAndView polymericTrade(@RequestParam Map<String, Object> params) throws Exception {
        //房协验证...验证开始...
        if(constants.getHouseVerifyEnable()){
            ValueUtil.verifyParams("acctIds,instanceId", params.get("acctIds"), params.get("instanceId"));
            String acctIds = params.get("acctIds") != null ? params.get("acctIds").toString() : null;
            String totalAmt = (String) params.get("amount");
            String res = supervisionFeign.validatePolymericTrade(acctIds, totalAmt);
            if (res == null) {
                ValueUtil.isError("数据不存在");
            }
//        if (!"".equals(ValueUtil.getFromJson(res, "data"))) {
//            ValueUtil.isError(ValueUtil.getFromJson(res, "data", "content"));
//        }
            JSONObject e = JSON.parseObject(res);
            String code = e.getString("code");
            if (!code.equals("200")) {
                String msg = e.getString("msg");
                ValueUtil.isError(msg);
            }

        }
        //...验证结束...
        ValueUtil.verifyParams("instanceId,channel,orderNo,amount",
                params.get("instanceId"), params.get("channel"),
                params.get("orderNo"), params.get("amount"));
        HttpServletResponse response = HttpServletUtil.getResponse();
        String paymentChannel = (String) params.get("channel");
        Map<String, Object> requestMap = new HashedMap();
        requestMap.putAll(params);
        Instance instance = instanceService.findByInstanceIdAndType((String) params.get("instanceId"), InstanceTypeEnum.CONSUME.getCode());
        if (instance == null) {
            ValueUtil.isError("功能尚未开通");
        }
        Channel channel = Channel.getChannel(instance.getChannel());
        if (channel.equals(Channel.XY)) {
            ValueUtil.verifyParams("bankNo,isCompany,receiveAcct,receiveAcctName",
                    params.get("bankNo"), params.get("isCompany"), params.get("receiveAcct"), params.get("receiveAcctName"));
        } else if (channel.equals(Channel.Union)) {
            ValueUtil.verifyParams("number", params.get("number"));
        }
        if (paymentChannel.equals("wechat_csb") || paymentChannel.equals("alipay_csb")) {
            ValueUtil.verifyParams("returnUrl", params.get("returnUrl"));
        } else if (paymentChannel.equals("wechat_wap")) {
            ValueUtil.verifyParams("openid", params.get("openid"));
        }

        params.put("instance", instance);
        Object result = null;
        Map<String, Object> resultMap = null;
        try {
            PayService payService = payFactory.getInstance(channel);
            result = payService.polymericPay(params);
            resultMap = (Map<String, Object>) result;

            if (paymentChannel.equals("wechat_csb")) {
                resultMap.put("reqMap", JSONUtil.objectToJsonStr(requestMap));
                return new ModelAndView("/pay/payment", resultMap);
            } else if (paymentChannel.equals("alipay_csb")) {
                resultMap.put("reqMap", JSONUtil.objectToJsonStr(requestMap));
                return new ModelAndView("/pay/payment", resultMap);
            }
        } catch (CustomException ce) {
            if (resultMap == null) {
                resultMap = new HashedMap();
            }
            resultMap.put("errMsg", ce.getMessage());
            if (paymentChannel.equals("wechat_csb")) {
                resultMap.put("channel", "微信支付");
                return new ModelAndView("/pay/paymentError", resultMap);
            } else if (paymentChannel.equals("alipay_csb")) {
                resultMap.put("channel", "支付宝支付");
                return new ModelAndView("/pay/paymentError", resultMap);
            } else {
                ValueUtil.isError(ce.getMessage());
            }
        }

        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = null;
        out = response.getOutputStream();
        out.write(ValueUtil.toJson(result).getBytes("UTF-8"));
        return null;
    }

    /*
     * 查看支付状态
     * */
    @RequestMapping("/scrt/check/payment/status")
    @ResponseBody
    public String checkPaymentStatus(@RequestParam Map<String, Object> params) throws Exception {
        ValueUtil.verifyParams("instanceId,orderNo",
                params.get("instanceId"), params.get("orderNo"));
        Instance instance = instanceService.findByInstanceIdAndType((String) params.get("instanceId"), InstanceTypeEnum.CONSUME.getCode());
        if (instance == null) {
            ValueUtil.isError("功能尚未开通");
        }
        Channel channel = Channel.getChannel(instance.getChannel());
        params.put("instance", instance);
        PayService payService = payFactory.getInstance(channel);
        Object result = payService.checkPaymentStatus(params);
        return ValueUtil.toJson(result);
    }

    /*
     * 刷新二维码
     * */
    @RequestMapping("/scrt/refresh/code")
    @ResponseBody
    public String refreshCode(@RequestParam Map<String, Object> params) throws Exception {
        Instance instance = instanceService.findByInstanceIdAndType((String) params.get("instanceId"), InstanceTypeEnum.CONSUME.getCode());
        if (instance == null) {
            ValueUtil.isError("功能尚未开通");
        }
        Channel channel = Channel.getChannel(instance.getChannel());
        PayService payService = payFactory.getInstance(channel);
        params.put("instance", instance);
        Object result = payService.polymericPay(params);
        String paymentChannel = (String) params.get("channel");
        HttpServletResponse response = HttpServletUtil.getResponse();
        HttpServletRequest request = HttpServletUtil.getRequests();
        Map<String, Object> resultMap = (Map<String, Object>) result;
        String img = (String) resultMap.get("img");
        return ValueUtil.toJson(img);
    }


    /*
     * 退款
     * */
    @PostMapping("/scrt/refund/trade")
    public String refundTrade(@RequestParam Map<String, Object> params) throws Exception {
        ValueUtil.verifyParams("instanceId,orderNo,amount",
                params.get("instanceId"), params.get("orderNo"), params.get("amount"));
        Instance instance = instanceService.findByInstanceIdAndType((String) params.get("instanceId"), InstanceTypeEnum.CONSUME.getCode());
        if (instance == null) {
            ValueUtil.isError("功能尚未开通");
        }
        Channel channel = Channel.getChannel(instance.getChannel());
        PayService payService = payFactory.getInstance(channel);
        Object result = payService.refundTrade(params);

        return ValueUtil.toJson();
    }

}
