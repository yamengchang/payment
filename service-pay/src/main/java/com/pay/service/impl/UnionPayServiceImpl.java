package com.pay.service.impl;

import com.alibaba.fastjson.*;
import com.google.gson.*;
import com.omv.common.util.basic.*;
import com.omv.common.util.date.DateUtil;
import com.omv.common.util.error.*;
import com.omv.common.util.signature.*;
import com.omv.redis.config.jedis.*;
import com.pay.bean.*;
import com.pay.dao.*;
import com.pay.entity.*;
import com.pay.service.*;
import com.pay.utils.*;
import com.pay.utils.union.report.HttpClient;
import com.pay.utils.union.report.*;
import com.pay.utils.union.sdk.*;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.message.*;
import org.apache.http.util.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.util.Base64Utils;

import javax.servlet.http.*;
import javax.transaction.*;
import java.io.*;
import java.text.*;
import java.util.*;

/**
 * Created by zwj on 2018/10/18.
 */
@Service
public class UnionPayServiceImpl extends PayFactory implements PayService {


    static {
        SDKConfig.getConfig().loadPropertiesFromSrc();
    }

    @Autowired
    private InstanceService instanceService;
    @Autowired
    private BusiAccountService busiAccountService;
    @Autowired
    private PaymentInfoService paymentInfoService;
    @Autowired
    private LeaseSerialRecordService serialRecordService;
    @Autowired
    private PaymentChannelService channelService;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private Constants constants;
    @Autowired
    private InstanceExpandUnionDao instanceExpandUnionDao;
    @Autowired
    private ConvenientPaymentRepository convenientPaymentRepository;
    @Autowired
    private ResourcePicDao resourcePicDao;

    @Value("${acpsdk.idCardAuth}")
    private String ocrUrl;
    @Value("${acpsdk.token}")
    private String ocrToken;

    @Override
    public Object queryDetail(String orderId) {
        return null;
    }

    @Override
    public Object refund(String orderNumber, String refundNumber, Double payAmount, Double refundAmount, String title, String description) {
        return null;
    }

    @Override
    public Object appPay(String orderNumber, Double amount, String title, String description, String userId, HttpServletRequest request) {
        return null;
    }

    @Override
    public Object consume(Map<String, String> params) {
        return null;
    }

    @Override
    public Object polymericPay(Map<String, Object> params) throws Exception {
        String channel = (String) params.get("channel");
        if (channel.equals("bank_h5")) {
            return bankCardTrans(params);
        } else {
            return unitePayTrans(params);
        }
    }

    //统一支付
    private Object unitePayTrans(Map<String, Object> params) throws Exception {
        String instanceId = (String) params.get("instanceId");
        String orgOrderNo = (String) params.get("orderNo");
        String amountStr = (String) params.get("amount");//订单金额（元）
        String channel = (String) params.get("channel");

        List<LeaseSerialRecord> serialRecordList = serialRecordService.findByInstanceIdAndOriOrderNo(instanceId, orgOrderNo);
        for (LeaseSerialRecord serialRecord : serialRecordList) {
            if (serialRecord.getStatus().equals(Constants.PAYMENT_STATUS_SUCCESS)) {
                ValueUtil.isError("该订单已支付成功！");
            }
        }

        //        Instance instance = instanceService.findByInstanceId(instanceId);
        InstanceExpandUnion expandUnion = instanceExpandUnionDao.findByInstanceId(instanceId);
        String acqPubKey = expandUnion.getAcqPubKey();
        String acqPreKey = expandUnion.getAcqPrvKey();
        //        PaymentInfo paymentInfo = paymentInfoService.findByInstanceId(instanceId);

        DefaultHttpClient httpClient = new SSLClient();
        HttpPost postMethod = new HttpPost(constants.getUnionTransUrl() + "/backTransReq");
        //组织请求参数
        String orderNo = "D" + IDUtil.getID();
        List<BasicNameValuePair> nvps = getRequestParams(params, orderNo, acqPreKey);
        postMethod.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        HttpResponse resp = httpClient.execute(postMethod);
        String str = EntityUtils.toString(resp.getEntity(), "UTF-8");
        int statusCode = resp.getStatusLine().getStatusCode();

        if (200 == statusCode) {
            Map<String, String> responseMap = SignTermUtil.verferSignData(str, acqPubKey);
            System.out.println("验签成功");
            if (!(responseMap.get("respCode").equals("P000") || responseMap.get("respCode").equals("0000"))) {
                String respMsg = responseMap.get("respDesc");
                ValueUtil.isError(respMsg);
            }
            saveSerial(params, true);
            cacheTransRecord(instanceId, "PROCESSING", orderNo, orgOrderNo, amountStr, expandUnion.getUnionMerId());
            return returnMsg(channel, responseMap);
        }
        System.out.println("返回错误码:" + statusCode);
        saveSerial(params, false);

        return null;
    }

    private List<BasicNameValuePair> getRequestParams(Map<String, Object> params, String orderNo, String acqPreKey) throws Exception {
        String instanceId = (String) params.get("instanceId");
        String orgOrderNo = (String) params.get("orderNo");
        String acctNo = (String) params.get("acctNo");
        String customerName = (String) params.get("customerName");
        String returnUrl = (String) params.get("returnUrl");
        String commodityName = (String) (params.get("subject") == null ? "订单支付" : params.get("subject"));
        String commodityDesc = (String) (params.get("body") == null ? "订单支付" : params.get("body"));
        String openid = (String) params.get("openid");//公众号用户openid
        String number = (String) params.get("number");
        String amountStr = (String) params.get("amount");//订单金额（元）
        Double amt = Double.valueOf(amountStr) * 100;
        String amount = String.valueOf(new Double(Math.round(amt)).intValue());
        String channel = (String) params.get("channel");
        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();

        BusiAccount busiAccount = busiAccountService.findByInstanceIdAndNumber(instanceId, number);
        if (busiAccount.getTerReportStatus().equals(AuditEnum.PASS.getCode())) {

        }
        if (StringUtils.contains(channel, "wechat")) {
            String wechatStatus = busiAccount.getWxEnabledStatus();
            if (!(null != wechatStatus && wechatStatus.equals(AuditEnum.PASS.getCode()))) {
                ValueUtil.isError("商户暂不支持微信收款");
            }
            nvps.add(new BasicNameValuePair("transId", "10"));//交易类型 微信/支付宝
        } else if (StringUtils.contains(channel, "alipay")) {
            String alipayStatus = busiAccount.getAliEnabledStatus();
            if (!(null != alipayStatus && alipayStatus.equals(AuditEnum.PASS.getCode()))) {
                ValueUtil.isError("商户暂不支持支付宝收款");
            }
            nvps.add(new BasicNameValuePair("transId", "10"));//交易类型 微信/支付宝
        } else if (StringUtils.contains(channel, "bank_wap") || StringUtils.contains(channel, "bank_app")) {
            nvps.add(new BasicNameValuePair("transId", "27"));//交易类型 云闪付
        } else if (channel.equals("bank_apl")) {
            nvps.add(new BasicNameValuePair("transId", "31"));//交易类型 云闪付
        }

        nvps.add(new BasicNameValuePair("requestNo", IDUtil.getID())); //请求流水号
        nvps.add(new BasicNameValuePair("version", "V2.0"));//版本号
        if (channel.equals("wechat_app")) {//微信 APP 支付
            nvps.add(new BasicNameValuePair("productId", "0108"));//产品类型
        } else if (channel.equals("wechat_csb")) {//微信扫码支付
            nvps.add(new BasicNameValuePair("productId", "0104"));//产品类型
        } else if (channel.equals("wechat_wap")) {//微信公众号支付
            nvps.add(new BasicNameValuePair("productId", "0105"));//产品类型
            nvps.add(new BasicNameValuePair("openid", openid));//产品类型
        } else if (channel.equals("alipay_js")) {//支付宝 JS 到账
        } else if (channel.equals("alipay_csb")) {//支付宝扫码支付
            nvps.add(new BasicNameValuePair("productId", "0109"));//产品类型
            nvps.add(new BasicNameValuePair("storeId", "S" + IDUtil.getID()));
            nvps.add(new BasicNameValuePair("terminalId", "T" + IDUtil.getID()));
        } else if (channel.equals("alipay_bar")) {//支付宝条码支付
        } else if (channel.equals("alipay_wid")) {//支付宝支付窗支付
            nvps.add(new BasicNameValuePair("productId", "0115"));//产品类型
        } else if (channel.equals("bank_app")) {//银联云闪付APP支付
            ValueUtil.verifyParams("accNo,customerName", acctNo, customerName);
            nvps.add(new BasicNameValuePair("productId", "0129"));//产品类型
            nvps.add(new BasicNameValuePair("acctNo", acctNo));//卡号
            nvps.add(new BasicNameValuePair("customerName", customerName));//持卡人姓名
        } else if (channel.equals("bank_wap")) {//银联云闪付WAP支付
            ValueUtil.verifyParams("accNo,customerName", acctNo, customerName);
            nvps.add(new BasicNameValuePair("productId", "0138"));//产品类型
            nvps.add(new BasicNameValuePair("acctNo", acctNo));//卡号
            nvps.add(new BasicNameValuePair("customerName", customerName));//持卡人姓名
            nvps.add(new BasicNameValuePair("frontUrl", returnUrl));//前台通知地址 （需设置为外网能访问 http https均可），支付成功后的页面 点击“返回商户”按钮的时候将异步通知报文post到该地址
        } else if (channel.equals("bank_apl")) {//Apple pay
            nvps.add(new BasicNameValuePair("productId", "0128"));//产品类型
        }

        nvps.add(new BasicNameValuePair("merNo", busiAccount.getMerNo()));//商户号
        nvps.add(new BasicNameValuePair("terMerNo", busiAccount.getTerMerNo()));//商户终端商户号
        //        orderDate = DateUtil.toString(DateUtil.toDate(txnTime,"yyyy-MM-dd"),"yyyyMMdd");
        String orderDate = DateUtil.toString(new Date(), "yyyyMMdd");

        nvps.add(new BasicNameValuePair("orderDate", orderDate));//订单日期

        nvps.add(new BasicNameValuePair("orderNo", orderNo));//商户订单号  由支付平台生成
        if (channel.indexOf("wechat") >= 0) {//微信支付
            nvps.add(new BasicNameValuePair("notifyUrl", constants.getPAYMENT_DOMAIN() + "/callback/union/wechat"));//异步通知地址
            nvps.add(new BasicNameValuePair("subMerNo", busiAccount.getMerNo() + busiAccount.getNumber()));//商户识别id
            nvps.add(new BasicNameValuePair("subMerName", busiAccount.getBusiName()));//收款商户名称
            nvps.add(new BasicNameValuePair("subChnlMerNo", busiAccount.getWxBankSpCode()));//子商户号，微信报件后返回的
        } else if (channel.indexOf("alipay") >= 0) {//支付宝支付
            nvps.add(new BasicNameValuePair("notifyUrl", constants.getPAYMENT_DOMAIN() + "/callback/union/alipay"));//异步通知地址
            nvps.add(new BasicNameValuePair("subChnlMerNo", busiAccount.getAliBankSpCode()));//子商户号，支付宝报件后返回的
        } else if (channel.indexOf("bank") >= 0) {//银行卡支付
            nvps.add(new BasicNameValuePair("notifyUrl", constants.getPAYMENT_DOMAIN() + "/callback/union/card"));//异步通知地址
        }
        nvps.add(new BasicNameValuePair("transAmt", amount));//交易金额
        nvps.add(new BasicNameValuePair("commodityName", commodityName));//商品名称
        nvps.add(new BasicNameValuePair("commodityDesc", commodityDesc));//商品描述
        nvps.add(new BasicNameValuePair("returnUrl", returnUrl));

        nvps.add(new BasicNameValuePair("signature", SignTermUtil.signData(nvps, acqPreKey)));//签名

        params.put("orderNo", orderNo);
        params.put("orgOrderNo", orgOrderNo);
        params.put("orderDate", orderDate);
        params.put("amount", amount);
        return nvps;
    }

    private Object returnMsg(String channel, Map<String, String> responseMap) {
        //发起支付
        Map<String, Object> result = new HashMap<>();
        if (channel.equals("wechat_csb")) {
            String wx_url = responseMap.get("codeUrl");
            result.put("channel", "微信支付");
            result.put("type", "wechat");
            result.put("img", QRCodeUtils.creatRrCode(wx_url, 200, 200));
            return result;
        } else if (channel.equals("alipay_csb")) {
            String ali_url = responseMap.get("codeUrl");
            result.put("channel", "支付宝支付");
            result.put("type", "alipay");
            result.put("img", QRCodeUtils.creatRrCode(ali_url, 200, 200));
            return result;
        } else if (channel.equals("bank_wap")) {
            String payInfo = responseMap.get("htmlInfo");
            result.put("channel", "云闪付 wap");
            result.put("type", "bank_wap");
            result.put("payInfo", payInfo);
            return result;
        } else if (channel.equals("bank_app")) {
            String tn = responseMap.get("tn");
            result.put("channel", "云闪付 App");
            result.put("type", "bank_app");
            result.put("payInfo", tn);
            return result;
        } else if (channel.equals("bank_apl")) {
            String tn = responseMap.get("tn");
            result.put("channel", "Apple Pay");
            result.put("type", "bank_apl");
            result.put("payInfo", tn);
            return result;
        } else if (channel.equals("wechat_wap")) {
            String payInfo = responseMap.get("payInfo");
            Map<String, Object> respMap = MapUtil.str2Map(payInfo);
            String jsapi_package = (String) respMap.get("package");
            String jsapi_paysign = (String) respMap.get("paySign");
            String jsapi_noncestr = (String) respMap.get("nonceStr");
            String jsapi_signtype = (String) respMap.get("signType");
            String jsapi_timestamp = (String) respMap.get("timeStamp");
            String jsapi_appid = (String) respMap.get("appId");
            result.put("jsapi_package", jsapi_package);
            result.put("jsapi_paysign", jsapi_paysign);
            result.put("jsapi_noncestr", jsapi_noncestr);
            result.put("jsapi_signtype", jsapi_signtype);
            result.put("jsapi_timestamp", jsapi_timestamp);
            result.put("jsapi_appid", jsapi_appid);
            return result;
        }

        return null;
    }

    //绑卡交易  H5快捷支付
    private Object bankCardTrans(Map<String, Object> params) throws Exception {
        String instanceId = (String) params.get("instanceId");
        String orgOrderNo = (String) params.get("orderNo");
        String commodityName = (String) (params.get("subject") == null ? "订单支付" : params.get("subject"));
        String number = (String) params.get("number");
        String cerdId = (String) params.get("cerdId");//证件号
        String appType = (String) params.get("appType");//1:app   2:web
        String acctNo = (String) params.get("acctNo");
        String customerName = (String) params.get("customerName");
        String amountStr = (String) params.get("amount");//订单金额（元）
        Double amt = Double.valueOf(amountStr) * 100;
        String amount = String.valueOf(new Double(Math.round(amt)).intValue());

        params.put("amount", amount);
        List<LeaseSerialRecord> serialRecordList = serialRecordService.findByInstanceIdAndOriOrderNo(instanceId, orgOrderNo);
        for (LeaseSerialRecord serialRecord : serialRecordList) {
            if (serialRecord.getStatus().equals(Constants.PAYMENT_STATUS_SUCCESS)) {
                ValueUtil.isError("该订单已支付成功！");
            }
        }

        //        Instance instance = instanceService.findByInstanceId(instanceId);
        InstanceExpandUnion expandUnion = instanceExpandUnionDao.findByInstanceId(instanceId);
        String acqPubKey = expandUnion.getAcqPubKey();
        String acqPreKey = expandUnion.getAcqPrvKey();
        //        PaymentInfo paymentInfo = paymentInfoService.findByInstanceId(instanceId);

        DefaultHttpClient httpClient = new SSLClient();
        HttpPost postMethod = new HttpPost(constants.getUnionTransUrl() + "/backTransReq");
        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();

        nvps.add(new BasicNameValuePair("requestNo", IDUtil.getID())); //请求流水号
        nvps.add(new BasicNameValuePair("version", "V1.0"));
        nvps.add(new BasicNameValuePair("productId", "0131"));
        nvps.add(new BasicNameValuePair("transId", "01"));
        nvps.add(new BasicNameValuePair("merNo", expandUnion.getUnionMerId()));
        String orderDate = DateUtil.toString(new Date(), "yyyyMMdd");
        nvps.add(new BasicNameValuePair("orderDate", orderDate));
        String orderNo = "D" + IDUtil.getID();
        nvps.add(new BasicNameValuePair("orderNo", orderNo));
        nvps.add(new BasicNameValuePair("notifyUrl", constants.getPAYMENT_DOMAIN() + "/callback/union/bank/h5"));
        //        nvps.add(new BasicNameValuePair("returnUrl", );
        nvps.add(new BasicNameValuePair("transAmt", amount));
        nvps.add(new BasicNameValuePair("commodityName", commodityName));
        nvps.add(new BasicNameValuePair("customerName", customerName));//持卡人姓名
        nvps.add(new BasicNameValuePair("cerdType", "01"));//证件类型
        nvps.add(new BasicNameValuePair("cerdId", cerdId));//证件号
        nvps.add(new BasicNameValuePair("acctNo", acctNo));//卡号
        nvps.add(new BasicNameValuePair("phoneNo", acctNo));//预留手机号
        nvps.add(new BasicNameValuePair("appType", appType));//app类型 1:app   2:web
        BusiAccount busiAccount = busiAccountService.findByInstanceIdAndNumber(instanceId, number);
        nvps.add(new BasicNameValuePair("terMerNo", busiAccount.getTerMerNo()));//终端商户号

        nvps.add(new BasicNameValuePair("signature", SignTermUtil.signData(nvps, acqPreKey)));
        postMethod.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        HttpResponse resp = httpClient.execute(postMethod);
        String str = EntityUtils.toString(resp.getEntity(), "UTF-8");
        int statusCode = resp.getStatusLine().getStatusCode();
        params.put("orderNo", orderNo);
        params.put("orgOrderNo", orgOrderNo);
        params.put("orderDate", orderDate);
        params.put("amount", amount);
        if (200 == statusCode) {
            String respCode = StringUtils.substringBetween(str, "respCode=", "&");
            if (!respCode.equals("0000")) {
                if (respCode.equals("0016")) {
                    ValueUtil.isError("该商家尚未开通银行卡收款！");
                }
            }
            Map<String, String> responseMap = SignTermUtil.verferSignData(str, acqPreKey);
            System.out.println("验签成功");

            saveSerial(params, true);
            cacheTransRecord(instanceId, "PROCESSING", orderNo, orgOrderNo, amountStr, expandUnion.getUnionMerId());
            return null;
        }
        return null;
    }


    private void cacheTransRecord(String instanceId, String status, String orderNo, String oriOrderNo, String amountStr, String merId) {
        PaymentResult result = new PaymentResult();
        result.setStatus(status);
        result.setAmount(amountStr);
        result.setMrch_id(merId);
        result.setOrder_no(oriOrderNo);
        result.setPay_order_no(orderNo);
        redisClient.set(instanceId + oriOrderNo, JSONUtil.objectToJsonStr(result), 3600);
    }

    private void saveSerial(Map<String, Object> params, boolean flag) {
        String instanceId = (String) params.get("instanceId");
        String number = (String) params.get("number");
        String orgOrderNo = (String) params.get("orgOrderNo");
        String orderNo = (String) params.get("orderNo");
        String userId = (String) params.get("userId");
        String orderDate = (String) params.get("orderDate");
        String amount = (String) params.get("amount");//订单金额
        String channel = (String) params.get("channel");

        PaymentChannel paymentChannel = channelService.findByInstanceIdAndChannelSign(instanceId, channel);
        String unionRateType = paymentChannel.getUnionRateType();
        LeaseSerialRecord serialRecord = new LeaseSerialRecord();
        serialRecord.setSeriaType(Constants.SERIAL_TYPE_RENT);
        serialRecord.setStatus(Constants.PAYMENT_STATUS_WAITING);
        serialRecord.setTransTime(orderDate);
        String proRate = "";
        Integer amtInt = Integer.valueOf(amount);
        if (unionRateType.equals("02")) {
            proRate = "0";
            String brokerage = paymentChannel.getFixed();
            Double brokerageDouble = Double.valueOf(brokerage);
            Integer brokerageInt = new Double(Math.round(brokerageDouble * 100)).intValue();
            Integer realIncome = amtInt;
            if (amtInt > brokerageInt) {
                realIncome = amtInt - brokerageInt;
            }
            serialRecord.setRealIncome(String.valueOf(realIncome));
            serialRecord.setBrokerage(String.valueOf(brokerage));
        } else if (unionRateType.equals("04")) {
            proRate = paymentChannel.getProRate();
            Double brokerageFloat = amtInt * Double.valueOf(proRate);//服务费
            Integer brokerageInt = new Float(Math.round(brokerageFloat)).intValue();
            Integer realIncome = amtInt - brokerageInt;
            serialRecord.setRealIncome(String.valueOf(realIncome));
            serialRecord.setBrokerage(String.valueOf(brokerageInt));
        } else if (unionRateType.equals("05")) {
            String lowest = paymentChannel.getFixed();
            Double fixedDouble = Double.valueOf(lowest);
            Integer fixedInt = new Double(Math.round(fixedDouble * 100)).intValue();

            proRate = paymentChannel.getProRate();
            Double brokerageDouble = amtInt * Double.valueOf(proRate);//服务费
            Integer brokerageInt = new Double(Math.round(brokerageDouble)).intValue();
            Integer realIncome = amtInt;
            if (brokerageInt > fixedInt) {
                realIncome = amtInt - brokerageInt;
                serialRecord.setBrokerage(String.valueOf(brokerageInt));
            } else {
                realIncome = amtInt - fixedInt;
                serialRecord.setBrokerage(String.valueOf(fixedInt));
            }
            serialRecord.setRealIncome(String.valueOf(realIncome));
        }
        serialRecord.setRate(proRate);
        serialRecord.setAmount(amount);
        serialRecord.setPaymentChannel(channel);
        serialRecord.setInstanceId(instanceId);
        serialRecord.setOrderNo(orderNo);
        serialRecord.setOriOrderNo(orgOrderNo);
        serialRecord.setBankChannel("union");
        serialRecordService.save(serialRecord);
    }

    @Override
    public Object convenientPayment(Map<String, Object> params) throws IOException {
        String channel = (String) params.get("channel");
        if (channel.equals("app")) {
            return convenientPaymentWithApp(params);
        } else if (channel.equals("gate")) {
            return convenientPaymentWithGate(params);
        } else if (channel.equals("back")) {
            return convenientPaymentWithBack(params);
        }
        return null;
    }

    @Override
    public Object convenientPaymentBatch(Map map) throws IOException {
        String instanceId = (String) map.get("instanceId");
        Instance instance = (Instance) map.get("instance");
        EncryptManager encryptManager = new EncryptManager(instance.getPrivateKey(), instance.getPublicKey());
        encryptManager.parseMobKey(map.get("mobKey").toString());
        String dataStr = encryptManager.decryptStr(map.get("data").toString());
        Map dataMap = new Gson().fromJson(dataStr, Map.class);
        List dataList = (List) dataMap.get("list");
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object o : dataList) {
            Map oMap = (Map) o;
            Map<String, Object> params = new HashMap<>();
            params.put("instanceId", instanceId);
            params.put("accNo", dataMap.get("accNo"));
            params.put("customerNm", dataMap.get("customerNm"));
            params.put("certIfTp", dataMap.get("certIfTp"));
            params.put("certIfId", dataMap.get("certIfId"));
            params.put("txnAmt", oMap.get("txnAmt"));
            params.put("bussCode", oMap.get("bussCode"));
            params.put("usrNum", oMap.get("usrNum"));
            params.put("origQryId", oMap.get("origQryId"));
            params.put("orderId", oMap.get("orderId"));
            Object o_ = convenientPaymentWithBack(params);
            Map<String, Object> objectMap = new HashMap<>(2);
            objectMap.put("orderId", oMap.get("orderId"));
            objectMap.put("return", o_);
            result.add(objectMap);
        }
        return result;
    }

    @Override
    public Object convenientSearch(Map<String, Object> params) throws IOException {
        String instanceId = (String) params.get("instanceId");
        String orderId = (String) params.get("orderId");
        //TODO 只判断成功的orderId，不判断重复
        List<ConvenientPayment> convenientPaymentList = convenientPaymentRepository.findAllByOrderIdAndInstanceIdAndResultOrderByCreateTimeDesc(orderId, instanceId, "0");
        if (!convenientPaymentList.isEmpty()) {
            return convenientPaymentList.get(0).getReturnMsg();
        }
        convenientPaymentList = convenientPaymentRepository.findAllByOrderIdAndInstanceIdAndResultOrderByCreateTimeDesc(orderId, instanceId, "1");

        if (convenientPaymentList.isEmpty()) {
            ValueUtil.isError("订单号错误");
        }
        ConvenientPayment convenientPayment = convenientPaymentList.get(0);
        InstanceExpandUnion expandUnion = instanceExpandUnionDao.findByInstanceId(instanceId);
        String signCert = expandUnion.getSignCert();
        String middleCert = expandUnion.getMiddleCert();
        String rootCert = expandUnion.getRootCert();
        String certPwd = expandUnion.getSignCertPwd();
        String merId = expandUnion.getUnionMerId();
        String txnTime = DateUtil.toString(new Date(), "yyyyMMddHHmmss");
        Map<String, String> data = new HashMap<>();
        data.put("version", DemoBase.version);        //版本号
        data.put("encoding", DemoBase.encoding);       //字符集编码 可以使用UTF-8,GBK两种方式
        data.put("signMethod", SDKConfig.getConfig().getSignMethod()); //签名方法
        data.put("txnType", "00");              //交易类型 00-默认
        data.put("txnSubType", "00");             //交易子类型 默认00
        data.put("bizType", "000501");            //业务类型 代收
        //商户接入参数
        data.put("merId", merId);          //商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
        data.put("accessType", "0");             //接入类型，商户接入固定填0，不需修改
        //要调通交易以下字段必须修改
        data.put("orderId", orderId);        //****商户订单号，每次发交易测试需修改为被查询的交易的订单号
        data.put("txnTime", txnTime);        //****订单发送时间，每次发交易测试需修改为被查询的交易的订单发送时间
        // 请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文------------->**/
        //Map<String, String> reqData = AcpService.sign(data,DemoBase.encoding);//报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        Map<String, String> reqData = AcpService.sign(data, signCert, certPwd);
        String url = SDKConfig.getConfig().getSingleQueryUrl();//交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.singleQueryUrl
        Map<String, String> rspData = AcpService.post(reqData, url, DemoBase.encoding); //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
        //TODO 如果成功
        convenientPayment.setReturnMsg("");
        convenientPayment.setResult("0");
        return doResult(rspData, middleCert, rootCert);
    }

    @Override
    public Object convenientSearchBatch(Map<String, Object> params) throws IOException {
        String instanceId = (String) params.get("instanceId");
        List orderIds = new Gson().fromJson((String) params.get("orderIds"), List.class);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object s : orderIds) {
            Map<String, Object> param = new HashMap<>();
            param.put("orderId", s.toString());
            param.put("instanceId", instanceId);
            Object e = convenientSearch(param);
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put("orderId", s.toString());
            objectMap.put("return", e);
            result.add(objectMap);
        }
        return result;
    }

    private Object convenientPaymentWithBack(Map<String, Object> params) throws IOException {
        String instanceId = (String) params.get("instanceId");
        InstanceExpandUnion expandUnion = instanceExpandUnionDao.findByInstanceId(instanceId);
        String signCert = expandUnion.getSignCert();
        String middleCert = expandUnion.getMiddleCert();
        String rootCert = expandUnion.getRootCert();
        String certPwd = expandUnion.getSignCertPwd();
        String merId = expandUnion.getUnionMerId();
        String txnTime = DateUtil.toString(new Date(), "yyyyMMddHHmmss");
        String txnAmt = (String) params.get("txnAmt");
        String bussCode = (String) params.get("bussCode");
        String usrNum = (String) params.get("usrNum");
        String origQryId = (String) params.get("origQryId");
        String billQueryInfo = "{\"usr_num\":\"" + usrNum + "\"}";
        String orderId = (String) params.get("orderId");
        String certIfTp = (String) params.get("certIfTp");
        String certIfId = (String) params.get("certIfId");
        String customerNm = (String) params.get("customerNm");
        String accNo = (String) params.get("accNo");
        String backUrl = (String) params.get("backUrl");
        String order = IDUtil.getID();
        if (certIfTp == null) {
            certIfTp = "01";
        }
        if (certIfId == null) {
            certIfId = "341126197709218366";
        }
        if (customerNm == null) {
            customerNm = "全渠道";
        }
        if (accNo == null) {
            accNo = "6216261000000000018";
        }

        Map<String, String> data = new HashMap<String, String>();
        //银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
        data.put("version", DemoBase.version);                  //版本号
        data.put("encoding", DemoBase.encoding);           //字符集编码 可以使用UTF-8,GBK两种方式
        data.put("signMethod", SDKConfig.getConfig().getSignMethod()); //签名方法
        data.put("txnType", "13");                              //交易类型 13 账单支付
        data.put("txnSubType", "01");                           //交易子类型 01 便民缴费
        data.put("bizType", "000601");                          //业务类型
        data.put("channelType", "07");                          //渠道类型07-PC
        data.put("accessType", "0");
        data.put("currencyCode", "156");

        //商户接入参数***/
        data.put("merId", merId);                     //商户号码（商户号码777290058110097仅做为测试调通交易使用，该商户号配置了需要对敏感信息加密）测试时请改成自己申请的商户号，【自己注册的测试777开头的商户号不支持代收产品】
        data.put("orderId", order);                //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
        data.put("txnTime", txnTime);            //订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
        data.put("txnAmt", txnAmt);
        if (null != origQryId && !"".equals(origQryId)) {
            data.put("origQryId", origQryId);//账单要素，根据前文显示要素列表由用户填写值，此处默认取demo演示页面传递的参数
        }

        data.put("bussCode", bussCode);                   //
        data.put("billQueryInfo", AcpService.base64Encode(billQueryInfo, DemoBase.encoding));

        Map<String, String> customerInfoMap = new HashMap<String, String>();
        customerInfoMap.put("certifTp", certIfTp);//证件类型
        customerInfoMap.put("certifId", certIfId);//证件号码
        customerInfoMap.put("customerNm", customerNm);//姓名
        //        customerInfoMap.put("phoneNo", "13552535506");    //手机号
        //        customerInfoMap.put("pin", AcpService.encryptPin("6216261000000000018","123456", DemoBase.encoding));//密码
        //        customerInfoMap.put("cvn2", "123");           //卡背面的cvn2三位数字
        //        customerInfoMap.put("expired", "1711");      //有效期 年在前月在后

        /////////不对敏感信息加密使用：
        //        contentData.put("accNo", "6216261000000000018");            //这里测试的时候使用的是测试卡号，正式环境请使用真实卡号
        //        String customerInfoStr = AcpService.getCustomerInfo(customerInfoMap,null,DemoBase.encoding_UTF8);
        ////////

        //////////如果商户号开通了  商户对敏感信息加密的权限那么，需要对 卡号accNo，pin和phoneNo，cvn2，expired加密（如果这些上送的话），对敏感信息加密使用：
        data.put("encryptCertId", AcpService.getEncryptCertId());
        //        String accNo = AcpService.encryptData("6216261000000000018", DemoBase.encoding);
        data.put("accNo", accNo);
        String customerInfoStr = AcpService.getCustomerInfoWithEncrypt(customerInfoMap, accNo, DemoBase.encoding);
        //////////

        data.put("customerInfo", customerInfoStr);
        data.put("backUrl", constants.getPAYMENT_DOMAIN() + "/callback/union/ylpay");//后台通知地址
        //TODO 只判断成功的orderId，不判断重复
        List<ConvenientPayment> convenientPaymentList = convenientPaymentRepository.findAllByOrderIdAndInstanceIdAndResultOrderByCreateTimeDesc(orderId, instanceId, "0");
        if (!convenientPaymentList.isEmpty()) {
            ValueUtil.isError("订单号不能重复");
        }
        ConvenientPayment convenientPayment = new ConvenientPayment();
        convenientPayment.setId(order);
        convenientPayment.setVersion(DemoBase.version);
        convenientPayment.setEncoding(DemoBase.encoding);
        convenientPayment.setSignMethod(SDKConfig.getConfig().getSignMethod());
        convenientPayment.setTxnType("13");
        convenientPayment.setTxnSubType("01");
        convenientPayment.setBizType("000601");
        convenientPayment.setChannelType("07");
        convenientPayment.setAccessType("0");
        convenientPayment.setCurrencyCode("156");
        convenientPayment.setMerId(merId);
        convenientPayment.setOrderId(orderId);
        convenientPayment.setInstanceId(instanceId);
        convenientPayment.setTxnTime(txnTime);
        convenientPayment.setTxnAmt(txnAmt);
        convenientPayment.setOrigQryId(origQryId);
        convenientPayment.setBussCode(bussCode);
        convenientPayment.setBillQueryInfo(data.get("billQueryInfo"));
        convenientPayment.setChannel("back");
        convenientPayment.setUsrNum(usrNum);
        convenientPayment.setCertIfTp(certIfTp);
        convenientPayment.setCertIfId(certIfId);
        convenientPayment.setCustomerNm(customerNm);
        convenientPayment.setEncryptCertId(data.get("encryptCertId"));
        convenientPayment.setAccNo(accNo);
        convenientPayment.setCustomerInfo(customerInfoStr);
        convenientPayment.setBackUrl(backUrl);
        convenientPaymentRepository.save(convenientPayment);

        // 请求方保留域，
        // 透传字段，查询、通知、对账文件中均会原样出现，如有需要请启用并修改自己希望透传的数据。
        // 出现部分特殊字符时可能影响解析，请按下面建议的方式填写：
        // 1. 如果能确定内容不会出现&={}[]"'等符号时，可以直接填写数据，建议的方法如下。
        //data.put("reqReserved", "透传信息1|透传信息2|透传信息3");
        // 2. 内容可能出现&={}[]"'符号时：
        // 1) 如果需要对账文件里能显示，可将字符替换成全角＆＝｛｝【】“‘字符（自己写代码，此处不演示）；
        // 2) 如果对账文件没有显示要求，可做一下base64（如下）。
        //    注意控制数据长度，实际传输的数据长度不能超过1024位。
        //    查询、通知等接口解析时使用new String(Base64.decodeBase64(reqReserved), DemoBase.encoding);解base64后再对数据做后续解析。
        //data.put("reqReserved", Base64.encodeBase64String("任意格式的信息都可以".toString().getBytes(DemoBase.encoding)));

        //对请求参数进行签名并发送http post请求，接收同步应答报文**/
        Map<String, String> reqData = AcpService.sign(data, signCert, certPwd);
        String requestBackUrl = SDKConfig.getConfig().getJfBackRequestUrl();  //交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
        Map<String, String> rspData = AcpService.post(reqData, requestBackUrl, DemoBase.encoding); //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
        return doResult(rspData, middleCert, rootCert);
    }

    private Object convenientPaymentWithGate(Map<String, Object> params) throws IOException {
        String instanceId = (String) params.get("instanceId");
        InstanceExpandUnion expandUnion = instanceExpandUnionDao.findByInstanceId(instanceId);
        String signCert = expandUnion.getSignCert();
        String middleCert = expandUnion.getMiddleCert();
        String rootCert = expandUnion.getRootCert();
        String certPwd = expandUnion.getSignCertPwd();
        HttpServletRequest req = HttpServletUtil.getRequests();
        String merId = expandUnion.getUnionMerId();
        String txnTime = DateUtil.toString(new Date(), "yyyyMMddHHmmss");
        String txnAmt = req.getParameter("txnAmt");
        String bussCode = req.getParameter("bussCode");
        String usrNum = (String) params.get("usrNum");
        String billQueryInfo = "{\"usr_num\":\"" + usrNum + "\"}";
        String origQryId = req.getParameter("origQryId");
        String orderId = (String) params.get("orderId");
        String backUrl = (String) params.get("backUrl");
        String order = IDUtil.getID();

        Map<String, String> data = new HashMap<String, String>();
        //银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
        data.put("version", DemoBase.version);                  //版本号
        data.put("encoding", DemoBase.encoding);           //字符集编码 可以使用UTF-8,GBK两种方式
        data.put("signMethod", SDKConfig.getConfig().getSignMethod()); //签名方法
        data.put("txnType", "13");                              //交易类型 13 账单支付
        data.put("txnSubType", "01");                           //交易子类型 01 便民缴费
        data.put("bizType", "000601");                          //业务类型
        data.put("channelType", "07");                          //渠道类型07-PC
        data.put("accessType", "0");                            //商户接入填0

        //商户接入参数***/
        data.put("merId", merId);                     //商户号码（商户号码777290058110097仅做为测试调通交易使用，该商户号配置了需要对敏感信息加密）测试时请改成自己申请的商户号，【自己注册的测试777开头的商户号不支持代收产品】
        data.put("orderId", IDUtil.getID());                //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
        //        txnTime = DateUtil.toString(DateUtil.toDate(txnTime,"yyyy-MM-dd HH:mm:ss"),"yyyyMMddHHmmss");
        //        txnTime = DateUtil.toString(new Date(), "yyyyMMddHHmmss");
        data.put("txnTime", txnTime);            //订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
        data.put("currencyCode", "156");
        data.put("txnAmt", txnAmt);
        data.put("bussCode", bussCode);// 业务类型号，此处默认取demo演示页面传递的参数
        data.put("billQueryInfo", AcpService.base64Encode(billQueryInfo, DemoBase.encoding));// 账单要素，根据前文显示要素列表由用户填写值，此处默认取demo演示页面传递的参数
        if (null != origQryId && "".equals(origQryId)) {
            data.put("origQryId", origQryId);// 先查后缴送账单查询应答报文的queryId，直接缴费的不送
        }
        data.put("frontUrl", DemoBase.frontUrl);//后台通知地址
        data.put("backUrl", constants.getPAYMENT_DOMAIN() + "/callback/union/ylpay");//后台通知地址

        // 请求方保留域，
        // 透传字段，查询、通知、对账文件中均会原样出现，如有需要请启用并修改自己希望透传的数据。
        // 出现部分特殊字符时可能影响解析，请按下面建议的方式填写：
        // 1. 如果能确定内容不会出现&={}[]"'等符号时，可以直接填写数据，建议的方法如下。
        //data.put("reqReserved", "透传信息1|透传信息2|透传信息3");
        // 2. 内容可能出现&={}[]"'符号时：
        // 1) 如果需要对账文件里能显示，可将字符替换成全角＆＝｛｝【】“‘字符（自己写代码，此处不演示）；
        // 2) 如果对账文件没有显示要求，可做一下base64（如下）。
        //    注意控制数据长度，实际传输的数据长度不能超过1024位。
        //    查询、通知等接口解析时使用new String(Base64.decodeBase64(reqReserved), DemoBase.encoding);解base64后再对数据做后续解析。
        //data.put("reqReserved", Base64.encodeBase64String("任意格式的信息都可以".toString().getBytes(DemoBase.encoding)));


        // 订单超时时间。
        // 超过此时间后，除网银交易外，其他交易银联系统会拒绝受理，提示超时。 跳转银行网银交易如果超时后交易成功，会自动退款，大约5个工作日金额返还到持卡人账户。
        // 此时间建议取支付时的北京时间加15分钟。
        // 超过超时时间调查询接口应答origRespCode不是A6或者00的就可以判断为失败。

        data.put("payTimeout", new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis() + 15 * 60 * 1000));
        //TODO 只判断成功的orderId，不判断重复
        List<ConvenientPayment> convenientPaymentList = convenientPaymentRepository.findAllByOrderIdAndInstanceIdAndResultOrderByCreateTimeDesc(orderId, instanceId, "0");
        if (!convenientPaymentList.isEmpty()) {
            ValueUtil.isError("订单号不能重复");
        }
        ConvenientPayment convenientPayment = new ConvenientPayment();
        convenientPayment.setId(order);
        convenientPayment.setVersion(DemoBase.version);
        convenientPayment.setEncoding(DemoBase.encoding);
        convenientPayment.setSignMethod(SDKConfig.getConfig().getSignMethod());
        convenientPayment.setTxnType("13");
        convenientPayment.setTxnSubType("01");
        convenientPayment.setBizType("000601");
        convenientPayment.setChannelType("07");
        convenientPayment.setAccessType("0");
        convenientPayment.setCurrencyCode("156");
        convenientPayment.setMerId(merId);
        convenientPayment.setOrderId(orderId);
        convenientPayment.setInstanceId(instanceId);
        convenientPayment.setTxnTime(txnTime);
        convenientPayment.setTxnAmt(txnAmt);
        convenientPayment.setOrigQryId(origQryId);
        convenientPayment.setBussCode(bussCode);
        convenientPayment.setBillQueryInfo(data.get("billQueryInfo"));
        convenientPayment.setChannel("back");
        convenientPayment.setEncryptCertId(data.get("encryptCertId"));
        convenientPayment.setBackUrl(backUrl);
        convenientPaymentRepository.save(convenientPayment);

        //请求参数设置完毕，以下对请求参数进行签名并生成html表单，将表单写入浏览器跳转打开银联页面**/
        //        Map<String, String> reqData = AcpService.sign(data, DemoBase.encoding);  //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        Map<String, String> reqData = AcpService.sign(data, signCert, certPwd);
        String requestFrontUrl = SDKConfig.getConfig().getJfFrontRequestUrl();    //获取请求银联的前台地址：对应属性文件acp_sdk.properties文件中的acpsdk.frontTransUrl
        String html = AcpService.createAutoFormHtml(requestFrontUrl, reqData, DemoBase.encoding);     //生成自动跳转的Html表单
        LogUtil.writeLog("打印请求HTML，此为请求报文，为联调排查问题的依据：" + html);
        System.out.println(html);    //将生成的html写到浏览器中完成自动跳转打开银联支付页面；这里调用signData之后，将html写到浏览器跳转到银联页面之前均不能对html中的表单项的名称和值进行修改，如果修改会导致验签不通过

        return html;
    }

    private Object convenientPaymentWithApp(Map<String, Object> params) throws IOException {
        String instanceId = (String) params.get("instanceId");
        InstanceExpandUnion expandUnion = instanceExpandUnionDao.findByInstanceId(instanceId);
        String signCert = expandUnion.getSignCert();
        String middleCert = expandUnion.getMiddleCert();
        String rootCert = expandUnion.getRootCert();
        String certPwd = expandUnion.getSignCertPwd();
        HttpServletRequest req = HttpServletUtil.getRequests();
        String merId = expandUnion.getUnionMerId();
        String txnTime = DateUtil.toString(new Date(), "yyyyMMddHHmmss");
        String txnAmt = req.getParameter("txnAmt");
        String bussCode = req.getParameter("bussCode");
        String usrNum = (String) params.get("usrNum");
        String billQueryInfo = "{\"usr_num\":\"" + usrNum + "\"}";
        String orderId = (String) params.get("orderId");
        String origQryId = req.getParameter("origQryId");
        String backUrl = (String) params.get("backUrl");
        String order = IDUtil.getID();

        Map<String, String> data = new HashMap<String, String>();
        //银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
        data.put("version", DemoBase.version);                  //版本号
        data.put("encoding", DemoBase.encoding);           //字符集编码 可以使用UTF-8,GBK两种方式
        data.put("signMethod", "01");                           //签名方法 目前只支持01-RSA方式证书加密
        data.put("txnType", "13");                              //交易类型 13 账单支付
        data.put("txnSubType", "01");                           //交易子类型 01 便民缴费
        data.put("bizType", "000601");                          //业务类型
        data.put("channelType", "08");                          //渠道类型07-PC
        data.put("accessType", "0");

        //商户接入参数***/
        data.put("merId", merId);                     //商户号码（商户号码777290058110097仅做为测试调通交易使用，该商户号配置了需要对敏感信息加密）测试时请改成自己申请的商户号，【自己注册的测试777开头的商户号不支持代收产品】
        data.put("orderId", IDUtil.getID());                //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
        //        txnTime = DateUtil.toString(DateUtil.toDate(txnTime,"yyyy-MM-dd HH:mm:ss"),"yyyyMMddHHmmss");
        //        txnTime = DateUtil.toString(new Date(), "yyyyMMddHHmmss");
        data.put("txnTime", txnTime);            //订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
        data.put("currencyCode", "156");
        data.put("txnAmt", txnAmt);
        data.put("bussCode", bussCode);// 业务类型号，此处默认取demo演示页面传递的参数
        data.put("billQueryInfo", AcpService.base64Encode(billQueryInfo, DemoBase.encoding));// 账单要素，根据前文显示要素列表由用户填写值，此处默认取demo演示页面传递的参数
        if (null != origQryId && "".equals(origQryId)) {
            data.put("origQryId", origQryId);// 先查后缴送账单查询应答报文的queryId，直接缴费的不送
        }
        data.put("backUrl", constants.getPAYMENT_DOMAIN() + "/callback/union/ylpay");//后台通知地址
        //TODO 只判断成功的orderId，不判断重复
        List<ConvenientPayment> convenientPaymentList = convenientPaymentRepository.findAllByOrderIdAndInstanceIdAndResultOrderByCreateTimeDesc(orderId, instanceId, "0");
        if (!convenientPaymentList.isEmpty()) {
            ValueUtil.isError("订单号不能重复");
        }
        ConvenientPayment convenientPayment = new ConvenientPayment();
        convenientPayment.setId(order);
        convenientPayment.setVersion(DemoBase.version);
        convenientPayment.setEncoding(DemoBase.encoding);
        convenientPayment.setSignMethod(SDKConfig.getConfig().getSignMethod());
        convenientPayment.setTxnType("13");
        convenientPayment.setTxnSubType("01");
        convenientPayment.setBizType("000601");
        convenientPayment.setChannelType("07");
        convenientPayment.setAccessType("0");
        convenientPayment.setCurrencyCode("156");
        convenientPayment.setMerId(merId);
        convenientPayment.setOrderId(orderId);
        convenientPayment.setInstanceId(instanceId);
        convenientPayment.setTxnTime(txnTime);
        convenientPayment.setTxnAmt(txnAmt);
        convenientPayment.setOrigQryId(origQryId);
        convenientPayment.setBussCode(bussCode);
        convenientPayment.setBillQueryInfo(data.get("billQueryInfo"));
        convenientPayment.setChannel("back");
        convenientPayment.setEncryptCertId(data.get("encryptCertId"));
        convenientPayment.setBackUrl(backUrl);
        convenientPaymentRepository.save(convenientPayment);

        //对请求参数进行签名并发送http post请求，接收同步应答报文**/
        //        Map<String, String> reqData = AcpService.sign(data, DemoBase.encoding);            //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        Map<String, String> reqData = AcpService.sign(data, signCert, certPwd);
        String requestBackUrl = SDKConfig.getConfig().getJfAppRequestUrl();            //交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
        Map<String, String> rspData = AcpService.post(reqData, requestBackUrl, DemoBase.encoding); //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

        //对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
        //应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
        return doResult(rspData, middleCert, rootCert);
    }

    private Object doResult(Map<String, String> rspData, String middleCert, String rootCert) throws IOException {
        if (!rspData.isEmpty()) {
            if (AcpService.validateSign(rspData, middleCert, rootCert, DemoBase.encoding)) {
                LogUtil.writeLog("验证签名成功");
                String respCode = rspData.get("respCode");
                if (("00").equals(respCode)) {
                    //成功
                    //TODO
                    if (rspData.get("billDetailInfo") != null) {
                        String queryId = rspData.get("queryId");
                        String billDetailInfo = AcpService.base64Decode(rspData.get("billDetailInfo"), DemoBase.encoding);

                        String respJson = "{\"queryId\":\"" + queryId + "\",\"billDetailInfo\":" + billDetailInfo + "}";
                        System.out.println("respJson:" + respJson);
                    }
                    return rspData;
                } else {
                    //其他应答码为失败请排查原因或做失败处理
                    System.out.println(respCode);
                    return rspData;
                    //TODO
                }
            } else {
                LogUtil.writeErrorLog("验证签名失败");
                return "验证签名失败";
                //TODO 检查验证签名失败的原因
            }
        } else {
            //未返回正确的http状态
            LogUtil.writeErrorLog("未获取到返回报文或返回http状态码非200");
        }
        ValueUtil.isError("出错了");
        return ValueUtil.toJson();
    }

    @Override
    public Object convenientJfBill(Map<String, Object> params) throws IOException {
        String instanceId = (String) params.get("instanceId");
        //        Instance instance = (Instance) params.get("instance");
        InstanceExpandUnion expandUnion = instanceExpandUnionDao.findByInstanceId(instanceId);
        String signCert = expandUnion.getSignCert();
        String middleCert = expandUnion.getMiddleCert();
        String rootCert = expandUnion.getRootCert();
        String certPwd = expandUnion.getSignCertPwd();
        ;

        //        PaymentInfo paymentInfo = paymentInfoService.findByInstanceId(instanceId);
        HttpServletRequest req = HttpServletUtil.getRequests();
        String merId = expandUnion.getUnionMerId();
        String usrNum = (String) params.get("usrNum");
        String billQueryInfo = "{\"usr_num\":\"" + usrNum + "\"}";
        String bussCode = req.getParameter("bussCode");

        Map<String, String> data = new HashMap<String, String>();

        //银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改
        data.put("version", DemoBase.version);                  //版本号
        data.put("encoding", DemoBase.encoding);           //字符集编码 可以使用UTF-8,GBK两种方式
        data.put("signMethod", SDKConfig.getConfig().getSignMethod()); //签名方法
        data.put("txnType", "73");                              //交易类型 73 账单查询
        data.put("txnSubType", "01");                           //交易子类型 01 便民缴费
        data.put("bizType", "000601");                          //业务类型
        data.put("channelType", "07");                          //渠道类型
        data.put("accessType", "0");

        //商户接入参数
        data.put("merId", merId);                               //商户号码（商户号码777290058110097仅做为测试调通交易使用，该商户号配置了需要对敏感信息加密）测试时请改成自己申请的商户号，【自己注册的测试777开头的商户号不支持代收产品】
        data.put("orderId", IDUtil.getID());                           //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
        String txnTime = DateUtil.toString(new Date(), "yyyyMMddHHmmss");
        data.put("txnTime", txnTime);                           //订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效

        data.put("bussCode", bussCode);// 业务类型号，此处默认取demo演示页面传递的参数
        data.put("billQueryInfo", AcpService.base64Encode(billQueryInfo, DemoBase.encoding));// 账单要素，根据前文显示要素列表由用户填写值，此处默认取demo演示页面传递的参数


        //对请求参数进行签名并发送http post请求，接收同步应答报文
        Map<String, String> reqData = AcpService.sign(data, signCert, certPwd);            //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。

        //        Map<String, String> reqData = AcpService.sign(data, DemoBase.encoding);            //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        String requestBackUrl = SDKConfig.getConfig().getJfBackRequestUrl();            //交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
        Map<String, String> rspData = AcpService.post(reqData, requestBackUrl, DemoBase.encoding); //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

        //对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考-------------
        //应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
        return doResult(rspData, middleCert, rootCert);
    }

    @Override
    public Object convenientJfBillBatch(Map<String, Object> params) throws IOException {
        String instanceId = (String) params.get("instanceId");
        List dataList = new Gson().fromJson((String) params.get("list"), List.class);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object o : dataList) {
            Map oMap = (Map) o;
            Map<String, Object> param = new HashMap<>();
            param.put("instanceId", instanceId);
            param.put("bussCode", oMap.get("bussCode"));
            param.put("usrNum", oMap.get("usrNum"));
            Object o_ = convenientPaymentWithBack(param);
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put("usrNum", oMap.get("usrNum"));
            objectMap.put("return", o_);
            result.add(objectMap);
        }
        return result;
    }

    /*
     *@Author : Gavin
     *@Email : gavinsjq@sina.com
     *@Date: 2018/10/26 9:52
     *@Description : 报件
     *@Params :  * @param null
     */
    @Override
    @Transactional
    public Object createReport(BusiAccount busiAccount) throws Exception {
        ValueUtil.verifyParams("instanceId,busiName,contactName,provinceId,cityId,cardNoCipher,isCompay,accBankNo,address,merNameAlias,servicePhone," + "contactEmail,contactPhone,cerdId,cardName,number,attr1,attr2,attr3,attr4", busiAccount);
        if (busiAccount.getAliIsEnabled()) {
            ValueUtil.verifyParams("aliProfCode", busiAccount.getAliProfCode());
        }
        if (busiAccount.getWxIsEnabled()) {
            ValueUtil.verifyParams("wxProfCode", busiAccount.getWxProfCode());
        }
        BusiAccount dbBusiAccount = busiAccountService.findByInstanceIdAndNumber(busiAccount.getInstanceId(), busiAccount.getNumber());
        if (null != dbBusiAccount) {
            ValueUtil.isError("该店铺已进行报件，报件失败");
        }
        PaymentInfo paymentInfo = paymentInfoService.findByInstanceId(busiAccount.getInstanceId());
        InstanceExpandUnion expandUnion = instanceExpandUnionDao.findByInstanceId(busiAccount.getInstanceId());
        busiAccount.setPaymentInfo(paymentInfo);
        busiAccount.setMerNo(expandUnion.getUnionMerId());

        String terNo = terminalReport(busiAccount, null);
        busiAccount.setTerMerNo(terNo);
        busiAccount.setTerReportStatus(AuditEnum.AUDITING.getCode());
        busiAccountService.save(busiAccount);

        String bankSpCode;
        if (busiAccount.getAliIsEnabled()) {//开通支付宝清分
            bankSpCode = createAliOrWxReport("WEIXIN", terNo, busiAccount);
            if (StringUtils.isNotEmpty(bankSpCode)) {
                busiAccount.setWxBankSpCode(bankSpCode);
                busiAccount.setWxEnabledStatus(AuditEnum.AUDITING.getCode());
            }
        }
        if (busiAccount.getWxIsEnabled()) {//开通微信清分
            bankSpCode = createAliOrWxReport("ALIPAY", terNo, busiAccount);
            if (StringUtils.isNotEmpty(bankSpCode)) {
                busiAccount.setAliBankSpCode(bankSpCode);
                busiAccount.setAliEnabledStatus(AuditEnum.AUDITING.getCode());
            }
        }
        busiAccountService.save(busiAccount);
        paymentInfoService.save(paymentInfo);
        return null;
    }

    @Override
    public Object checkReportStatus(Instance instance, String number) throws Exception {
        PaymentInfo paymentInfo = paymentInfoService.findByInstanceId(instance.getInstanceId());
        BusiAccount busiAccount = busiAccountService.findByInstanceIdAndNumber(instance.getInstanceId(), number);
        //查看终端报件状态
        if (!busiAccount.getTerReportStatus().equals(AuditEnum.SUCCESS.getCode())) {
            if (!chekTerReportStatus(instance, busiAccount)) {
                return false;
            }
        }

        if (null != busiAccount.getWxEnabledStatus() && busiAccount.getWxEnabledStatus().equals(EnableEnum.Enabling.getCode())) {
            checkReport("wechat", instance, paymentInfo, busiAccount);
        }
        if (null != busiAccount.getAliEnabledStatus() && busiAccount.getAliEnabledStatus().equals(EnableEnum.Enabling.getCode())) {
            checkReport("alipay", instance, paymentInfo, busiAccount);
        }
        return null;
    }

    private boolean chekTerReportStatus(Instance instance, BusiAccount busiAccount) throws Exception {
        InstanceExpandUnion expandUnion = instanceExpandUnionDao.findByInstanceId(instance.getInstanceId());

        String acqPubKey = expandUnion.getAcqPubKey();
        String acqPreKey = expandUnion.getAcqPrvKey();
        Map<String, String> textParam = new HashMap<String, String>();
        textParam.put("requestNo", IDUtil.getID());
        textParam.put("version", "V1.0");
        textParam.put("transId", "29");
        textParam.put("merNo", busiAccount.getMerNo());
        textParam.put("terMerNo", busiAccount.getTerMerNo());
        textParam.put("oriRequestNo", busiAccount.getOriRequestNo());

        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        Iterator<Map.Entry<String, String>> iterator = textParam.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            String key = next.getKey();
            String value = next.getValue();
            nvps.add(new BasicNameValuePair(key, value));
        }
        textParam.put("signature", SignTermUtil.signData(nvps, acqPreKey));

        String conHttp = HttpClient.conHttp(constants.getUnionTransUrl() + "/applyTranRequest", textParam, null, null);
        Map<String, String> responseMap = SignTermUtil.verferSignData(conHttp, acqPubKey);
        String respCode = responseMap.get("respCode");
        if (!respCode.equals("0000")) {
            String errMsg = responseMap.get("respDesc");
            ValueUtil.isError(errMsg);
        }
        String checkStatus = responseMap.get("checkStatus");
        if (checkStatus.equals("SUCCESS")) {
            busiAccount.setTerReportStatus(AuditEnum.SUCCESS.getCode());
            busiAccountService.save(busiAccount);
            return true;
        } else if (checkStatus.equals("FAIL")) {
            busiAccount.setTerReportStatus(AuditEnum.FAILED.getCode());
            busiAccountService.save(busiAccount);
            return false;
        } else if (checkStatus.equals("PASS")) {
            busiAccount.setTerReportStatus(AuditEnum.PASS.getCode());
            busiAccountService.save(busiAccount);
            return true;
        }
        return false;
    }

    @Override
    public Object updateReport(BusiAccount busiAccount) throws Exception {
        BusiAccount dbBusiAccount = busiAccountService.findByInstanceIdAndNumber(busiAccount.getInstanceId(), busiAccount.getNumber());
        String merNo = dbBusiAccount.getMerNo();
        String oriRequestNo = dbBusiAccount.getOriRequestNo();
        String terMerNo = dbBusiAccount.getTerMerNo();
        String merName = busiAccount.getBusiName();
        String contactName = busiAccount.getContactName();
        String cerdId = dbBusiAccount.getCerdId();
        String contactPhone = busiAccount.getContactPhone();
        String provinceId = busiAccount.getProvinceId();
        String cityId = busiAccount.getCityId();
        String cardNoCipher = busiAccount.getCardNoCipher();
        String address = busiAccount.getAddress();
        String accBankNo = busiAccount.getAccBankNo();
        String cardName = busiAccount.getCardName();
        String isCompay = busiAccount.getIsCompay();

        Instance instance = busiAccount.getInstance();

        InstanceExpandUnion expandUnion = instanceExpandUnionDao.findByInstanceId(instance.getInstanceId());
        String pubPath = expandUnion.getAcqPubKey();
        String prvPath = expandUnion.getAcqPrvKey();

        Map<String, String> textParam = new HashMap<String, String>();
        //定值
        textParam.put("version", "V1.0");  //版本号
        textParam.put("transId", "30"); //交易类型
        textParam.put("settleToType", "BANKCARD");  //结算方式  固定值: BANKCARD
        textParam.put("legalIdType", "IdCard"); //法人证件类型 IdCard表示身份证

        textParam.put("requestNo", IDUtil.getID());  //请求流水号
        textParam.put("merNo", merNo); //机构商户号  申请商户入网后，由收单机构发放
        textParam.put("terMerNo", terMerNo); //终端商户号

        textParam.put("terMerName", merName);  //终端商户名称  终端商户唯一标识
        textParam.put("address", address);  //终端商户所在详细地址
        textParam.put("legalPerson", contactName); //法人名称
        textParam.put("legalIdNo", cerdId);   //法人证件号码
        textParam.put("legalMobilePhone", contactPhone);   //法人手机号

        textParam.put("province", provinceId);    //终端商户所在省份，上送地区ID，如广东省，上送30
        textParam.put("city", cityId);    //终端商户所在城市，上送地区ID，如深圳市，上送44
        textParam.put("cardNoCipher", cardNoCipher); //结算账号   即银行卡号
        textParam.put("accBankNo", accBankNo); //联行号 结算账号所属联行号
        textParam.put("isCompay", isCompay); //是否对公账户  0：否1：是  ？？有什么区别
        textParam.put("cardName", cardName);  //结算账号户名
        textParam.put("oriRequestNo", oriRequestNo);  //原交易流水号

//		textParam.put("productAndRate", "[{\"product\":\"0103\",\"rate\":\"[0.00345]\",\"template\":\"04\"}]");//  产品和费率模板json字符串  ？？？
//查看当前实例下有支付渠道有哪些及相应的支付费率是多少
        List<PaymentChannel> paymentChannelList = channelService.findByInstanceId(instance.getInstanceId());
        JSONArray productAndRateArr = new JSONArray(paymentChannelList.size());
        for (PaymentChannel paymentChannel : paymentChannelList) {
            JSONObject productAndRateObj = new JSONObject();
            productAndRateObj.put("product", paymentChannel.getUnionProdId());
            productAndRateObj.put("rate", "[" + paymentChannel.getProRate() + "]");
            productAndRateObj.put("template", paymentChannel.getUnionRateType());
            productAndRateArr.add(productAndRateObj);
        }
        textParam.put("productAndRate", productAndRateArr.toJSONString());//  产品和费率模板json字符串

        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        Iterator<Map.Entry<String, String>> iterator = textParam.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            String key = next.getKey();
            String value = next.getValue();
            nvps.add(new BasicNameValuePair(key, value));
        }
        textParam.put("signature", SignTermUtil.signData(nvps, prvPath));


        Map<String, Object> fileParams = new HashMap<String, Object>();
        byte[] byte1 = new ByteArrayOutputStream() {
            @Override
            public synchronized byte[] toByteArray() {
                try {
                    InputStream a1 = busiAccount.getAttr1().getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int bytesRead = 0;
                    byte[] buffer = new byte[8192];
                    while ((bytesRead = a1.read(buffer, 0, 8192)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                    }

                    a1.close();
                    return bos.toByteArray();
                } catch (Exception e) {
                }
                return null;
            }
        }.toByteArray();

        byte[] byte2 = new ByteArrayOutputStream() {
            @Override
            public synchronized byte[] toByteArray() {
                try {
                    InputStream a1 = busiAccount.getAttr2().getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int bytesRead = 0;
                    byte[] buffer = new byte[8192];
                    while ((bytesRead = a1.read(buffer, 0, 8192)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                    }

                    a1.close();
                    return bos.toByteArray();
                } catch (Exception e) {
                }
                return null;
            }
        }.toByteArray();
        byte[] byte3 = new ByteArrayOutputStream() {
            @Override
            public synchronized byte[] toByteArray() {
                try {
                    InputStream a1 = busiAccount.getAttr3().getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int bytesRead = 0;
                    byte[] buffer = new byte[8192];
                    while ((bytesRead = a1.read(buffer, 0, 8192)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                    }

                    a1.close();
                    return bos.toByteArray();
                } catch (Exception e) {
                }
                return null;
            }
        }.toByteArray();
        byte[] byte4 = new ByteArrayOutputStream() {
            @Override
            public synchronized byte[] toByteArray() {
                try {
                    InputStream a1 = busiAccount.getAttr4().getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int bytesRead = 0;
                    byte[] buffer = new byte[8192];
                    while ((bytesRead = a1.read(buffer, 0, 8192)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                    }

                    a1.close();
                    return bos.toByteArray();
                } catch (Exception e) {
                }
                return null;
            }
        }.toByteArray();
        fileParams.put("attachment1", byte1);
        fileParams.put("attachment2", byte2);
        fileParams.put("attachment3", byte3);
        fileParams.put("attachment4", byte4);

        String conHttp = HttpClient.conHttp(constants.getUnionTransUrl() + "/applyTranRequest", textParam, fileParams, null);
        Map<String, String> responseMap = SignTermUtil.verferSignData(conHttp, pubPath);

//        resourcePicDao.deleteByResourceId(dbBusiAccount.getId());
        List<ResourcePic> picList = resourcePicDao.findByResourceId(dbBusiAccount.getId());
        if (!picList.isEmpty()) {
            resourcePicDao.deleteInBatch(picList);
        }
        if (null != byte1) {
            ResourcePic pic1 = new ResourcePic();
            pic1.setResourceId(busiAccount.getId());
            pic1.setContent(Base64Utils.encodeToString(byte1));
            resourcePicDao.save(pic1);
        }

        if (null != byte2) {
            ResourcePic pic2 = new ResourcePic();
            pic2.setResourceId(busiAccount.getId());
            pic2.setContent(Base64Utils.encodeToString(byte2));
            resourcePicDao.save(pic2);
        }
        if (null != byte3) {
            ResourcePic pic3 = new ResourcePic();
            pic3.setResourceId(busiAccount.getId());
            pic3.setContent(Base64Utils.encodeToString(byte3));
            resourcePicDao.save(pic3);
        }
        if (null != byte4) {
            ResourcePic pic4 = new ResourcePic();
            pic4.setResourceId(busiAccount.getId());
            pic4.setContent(Base64Utils.encodeToString(byte4));
            resourcePicDao.save(pic4);
        }

        dbBusiAccount.setBusiName(merName);
        dbBusiAccount.setContactName(contactName);
        dbBusiAccount.setCerdId(cerdId);
        dbBusiAccount.setContactPhone(contactPhone);
        dbBusiAccount.setProvinceId(provinceId);
        dbBusiAccount.setCityId(cityId);
        dbBusiAccount.setCardNoCipher(cardNoCipher);
        dbBusiAccount.setAddress(address);
        dbBusiAccount.setAccBankNo(accBankNo);
        dbBusiAccount.setCardName(cardName);
        dbBusiAccount.setIsCompay(isCompay);
        dbBusiAccount.setAliProfCode(busiAccount.getAliProfCode());
        dbBusiAccount.setWxProfCode(busiAccount.getWxProfCode());
        dbBusiAccount.setContactEmail(busiAccount.getContactEmail());
        dbBusiAccount.setTerReportStatus(AuditEnum.AUDITING.getCode());
        dbBusiAccount.setServicePhone(busiAccount.getServicePhone());
        dbBusiAccount.setAppletsAppId(busiAccount.getAppletsAppId());
        busiAccountService.save(dbBusiAccount);
        String respCode = responseMap.get("respCode");
        if (!respCode.equals("0000")) {
            String respMsg = responseMap.get("respDesc");
            ValueUtil.isError(respMsg);
        }
        dbBusiAccount.setInstance(instance);
        //判断是否需要进行支付宝和微信报件
        PaymentInfo paymentInfo = paymentInfoService.findByInstanceId(instance.getInstanceId());
        //没开通过支付宝、或开通拒绝，可进行支付宝报件
        if (busiAccount.getAliIsEnabled()) {
            if (null == paymentInfo.getAliIsEnabled() || dbBusiAccount.getAliEnabledStatus().equals(EnableEnum.UnEnabled.getCode())) {
                String bankSpCode = createAliOrWxReport("ALIPAY", dbBusiAccount.getTerMerNo(), dbBusiAccount);
                dbBusiAccount.setAliBankSpCode(bankSpCode);
            }
        }
        //没开通过微信、或开通拒绝，可进行微信报件
        if (busiAccount.getWxIsEnabled()) {
            if (null == paymentInfo.getWxIsEnabled() || dbBusiAccount.getWxEnabledStatus().equals(EnableEnum.UnEnabled.getCode())) {
                String bankSpCode = createAliOrWxReport("WEIXIN", dbBusiAccount.getTerMerNo(), dbBusiAccount);
                dbBusiAccount.setWxBankSpCode(bankSpCode);
            }
        }
        busiAccountService.save(dbBusiAccount);
        paymentInfoService.save(paymentInfo);

        return null;
    }

    @Override
    public Object refundTrade(Map<String, Object> params) {
        return null;
    }

    @Override
    public Object checkPaymentStatus(Map<String, Object> params) throws Exception {
        String instanceId = (String) params.get("instanceId");
        String oriOrderNo = (String) params.get("orderNo");
        String number = (String) params.get("number");

        //先从缓存中查看交易状态，若未查到，再走数据库
        String cache = redisClient.get(instanceId + oriOrderNo);
        if (StringUtils.isNotEmpty(cache)) {
            return MapUtil.str2Map(cache);
        }
        //        Instance instance = (Instance) params.get("instance");
        InstanceExpandUnion expandUnion = instanceExpandUnionDao.findByInstanceId(instanceId);
        String acqPubKey = expandUnion.getAcqPubKey();
        String acqPreKey = expandUnion.getAcqPrvKey();
        PaymentInfo paymentInfo = paymentInfoService.findByInstanceId(instanceId);
        List<LeaseSerialRecord> serialRecordList = serialRecordService.findByInstanceIdAndOriOrderNo(instanceId, oriOrderNo);
        if (serialRecordList.isEmpty()) {
            ValueUtil.isError("007", "该订单号不存在！");
        }

        PaymentResult result = new PaymentResult();
        result.setStatus("PROCESSING");
        result.setOrder_no(oriOrderNo);

        for (LeaseSerialRecord serialRecord : serialRecordList) {
            String status = serialRecord.getStatus();
            if (status.equals(Constants.PAYMENT_STATUS_SUCCESS)) {
                result.setStatus("SUCCESS");
                result.setTime_paid(serialRecord.getTransOverTime());
                result.setMrch_id(expandUnion.getUnionMerId());
                result.setPay_order_no(serialRecord.getOrderNo());
                result.setTransaction_no(serialRecord.getSerialNo());
                cacheTransRecord(instanceId, "SUCCESS", serialRecord.getOrderNo(), serialRecord.getOriOrderNo(), null, expandUnion.getUnionMerId());
                return result;
            } else if (status.equals(Constants.PAYMENT_STATUS_FAILED)) {
                result.setStatus("FAILED");
                result.setTime_paid(serialRecord.getTransOverTime());
                result.setMrch_id(expandUnion.getUnionMerId());
                result.setPay_order_no(serialRecord.getOrderNo());
                result.setTransaction_no(serialRecord.getSerialNo());
                cacheTransRecord(instanceId, "FAILED", serialRecord.getOrderNo(), serialRecord.getOriOrderNo(), null, expandUnion.getUnionMerId());
                return result;
            }
        }

        BusiAccount busiAccount = busiAccountService.findByInstanceIdAndNumber(instanceId, number);

        DefaultHttpClient httpClient = new SSLClient();
        HttpPost postMethod = new HttpPost(constants.getUnionTransUrl() + "/backTransReq");
        for (LeaseSerialRecord serialRecord : serialRecordList) {
            List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
            nvps.add(new BasicNameValuePair("requestNo", IDUtil.getID()));
            nvps.add(new BasicNameValuePair("merNo", expandUnion.getUnionMerId()));
            nvps.add(new BasicNameValuePair("terMerNo", busiAccount.getTerMerNo()));
            nvps.add(new BasicNameValuePair("version", "V2.0"));
            nvps.add(new BasicNameValuePair("transId", "04"));
            nvps.add(new BasicNameValuePair("orderDate", serialRecord.getTransTime()));
            nvps.add(new BasicNameValuePair("orderNo", serialRecord.getOrderNo()));
            nvps.add(new BasicNameValuePair("signature", SignTermUtil.signData(nvps, acqPreKey)));//签名
            postMethod.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            HttpResponse resp = httpClient.execute(postMethod);
            String str = EntityUtils.toString(resp.getEntity(), "UTF-8");
            int statusCode = resp.getStatusLine().getStatusCode();
            if (200 == statusCode) {
                Map<String, String> responseMap = SignTermUtil.verferSignData(str, acqPreKey);
                System.out.println("验签成功");
                String origRespCode = responseMap.get("origRespCode");
                if (origRespCode != null && (origRespCode.equals("0000") || origRespCode.equals("9998"))) {
                    String serialNo = responseMap.get("payId");
                    String transOverTime = responseMap.get("payTime");
                    String status = null;
                    if (origRespCode.equals("0000")) {
                        status = "SUCCESS";
                        serialRecord.setSerialNo(serialNo);
                        serialRecord.setStatus(Constants.PAYMENT_STATUS_SUCCESS);
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                        Date date = format.parse(transOverTime);
                        serialRecord.setTransOverTime(DateUtil.toString(date, "yyyy-MM-dd HH:mm:ss"));
                        serialRecordService.save(serialRecord);
                    } else if (origRespCode.equals("9998")) {
                        status = "FAILED";
                        serialRecord.setSerialNo(serialNo);
                        serialRecord.setStatus(Constants.PAYMENT_STATUS_FAILED);
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                        Date date = format.parse(transOverTime);
                        serialRecord.setTransOverTime(DateUtil.toString(date, "yyyy-MM-dd HH:mm:ss"));
                        serialRecordService.save(serialRecord);
                    }
                    result.setStatus(status);
                    result.setTime_paid(serialRecord.getTransOverTime());
                    result.setMrch_id(expandUnion.getUnionMerId());
                    result.setPay_order_no(serialRecord.getOrderNo());
                    result.setTransaction_no(serialRecord.getSerialNo());
                    cacheTransRecord(instanceId, status, serialRecord.getOrderNo(), serialRecord.getOriOrderNo(), null, expandUnion.getUnionMerId());
                }
            }

        }
        return result;
    }

    @Override
    public Object createAWReport(BusiAccount busiAccount) throws Exception {
        String number = busiAccount.getNumber();
        String instanceId = busiAccount.getInstanceId();
        BusiAccount dbBusiAccount = busiAccountService.findByInstanceIdAndNumber(instanceId, number);
        if(StringUtils.isNotEmpty(busiAccount.getAppletsAppId())){
            dbBusiAccount.setAppletsAppId(busiAccount.getAppletsAppId());
        }
        if(StringUtils.isNotEmpty(busiAccount.getMobileAppId())){
            dbBusiAccount.setMobileAppId(busiAccount.getMobileAppId());
        }
        if (dbBusiAccount.getWxEnabledStatus().equals(EnableEnum.Enabling.getCode())) {
            ValueUtil.isError("微信报件申请中，暂时无法操作！");
        } else if (dbBusiAccount.getWxEnabledStatus().equals(EnableEnum.Enabled.getCode())) {
            ValueUtil.isError("微信报件您已申请成功，无法再次操作！");
        }

        if (dbBusiAccount.getAliEnabledStatus().equals(EnableEnum.Enabling.getCode())) {
            ValueUtil.isError("支付宝报件申请中，暂时无法操作！");
        } else if (dbBusiAccount.getAliEnabledStatus().equals(EnableEnum.Enabled.getCode())) {
            ValueUtil.isError("支付宝报件您已申请成功，无法再次操作！");
        }
        //判断是否需要进行支付宝和微信报件
        PaymentInfo paymentInfo = paymentInfoService.findByInstanceId(instanceId);
        dbBusiAccount.setInstance(busiAccount.getInstance());
        //没开通过支付宝、或开通拒绝，可进行支付宝报件
        if (busiAccount.getAliIsEnabled()) {
            if (null == paymentInfo.getAliIsEnabled() || dbBusiAccount.getAliEnabledStatus().equals(EnableEnum.UnEnabled.getCode())) {
                String bankSpCode = createAliOrWxReport("ALIPAY", dbBusiAccount.getTerMerNo(), dbBusiAccount);
                dbBusiAccount.setAliBankSpCode(bankSpCode);
                dbBusiAccount.setAliEnabledStatus(AuditEnum.AUDITING.getCode());
            }
        }
        //没开通过微信、或开通拒绝，可进行微信报件
        if (busiAccount.getWxIsEnabled()) {
            if (null == paymentInfo.getWxIsEnabled() || dbBusiAccount.getWxEnabledStatus().equals(EnableEnum.UnEnabled.getCode())) {
                String bankSpCode = createAliOrWxReport("WEIXIN", dbBusiAccount.getTerMerNo(), dbBusiAccount);
                dbBusiAccount.setWxBankSpCode(bankSpCode);
                dbBusiAccount.setWxEnabledStatus(AuditEnum.AUDITING.getCode());
            }
        }
        busiAccountService.save(dbBusiAccount);
        paymentInfoService.save(paymentInfo);
        return null;
    }

    @Override
    public Object picUpload(Map<String, Object> params) {
        return null;
    }

    private void checkReport(String channel, Instance instance, PaymentInfo paymentInfo, BusiAccount busiAccount) throws Exception {
        InstanceExpandUnion expandUnion = instanceExpandUnionDao.findByInstanceId(instance.getInstanceId());
        String acqPubKey = expandUnion.getAcqPubKey();
        String acqPreKey = expandUnion.getAcqPrvKey();
        DefaultHttpClient httpClient = new SSLClient();
        HttpPost postMethod = new HttpPost(constants.getUnionTransUrl() + "/backTransReq");
        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("requestNo", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())));//请求流水号
        nvps.add(new BasicNameValuePair("transId", "48"));//交易类型
        nvps.add(new BasicNameValuePair("merNo", busiAccount.getMerNo()));//机构商户号
        nvps.add(new BasicNameValuePair("terMerNo", busiAccount.getTerMerNo()));//终端商户号
        if (channel.equals("alipay")) {
            nvps.add(new BasicNameValuePair("bankSpCode", busiAccount.getAliBankSpCode()));//子商户号
        } else if (channel.equals("wechat")) {
            nvps.add(new BasicNameValuePair("bankSpCode", busiAccount.getWxBankSpCode()));//子商户号
        }

        nvps.add(new BasicNameValuePair("signature", SignTermUtil.signData(nvps, acqPreKey)));//签名
        postMethod.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        HttpResponse resp = httpClient.execute(postMethod);
        String str = EntityUtils.toString(resp.getEntity(), "UTF-8");
        System.out.println(str);
        int statusCode = resp.getStatusLine().getStatusCode();
        if (200 == statusCode) {
            String data[] = str.split("&");
            StringBuffer buf = new StringBuffer();
            String signature = "";
            Map<String, String> responseMap = new HashMap<>();
            for (int i = 0; i < data.length; i++) {
                String tmp[] = data[i].split("=", 2);
                responseMap.put(tmp[0], tmp[1]);
                if ("signature".equals(tmp[0])) {
                    signature = tmp[1];
                } else {
                    buf.append(tmp[0]).append("=").append(tmp[1]).append("&");
                }
            }
            String code = responseMap.get("respCode");
            if (code.equals("0000")) {
                responseMap = SignTermUtil.verferSignData(str, acqPubKey);
                if (channel.equals("alipay")) {
                    busiAccount.setAliEnabledStatus(AuditEnum.PASS.getCode());
                } else if (channel.equals("wechat")) {
                    busiAccount.setWxEnabledStatus(AuditEnum.PASS.getCode());
                }
            } else if (code.equals("9998")) {
                if (channel.equals("alipay")) {
                    busiAccount.setAliEnabledStatus(AuditEnum.FAILED.getCode());
                } else if (channel.equals("wechat")) {
                    busiAccount.setWxEnabledStatus(AuditEnum.FAILED.getCode());
                }
            }
            busiAccountService.save(busiAccount);
            paymentInfoService.save(paymentInfo);
            return;
        }
        System.out.println("返回错误码:" + statusCode);
    }


    /*
     *@Author : Gavin
     *@Email : gavinsjq@sina.com
     *@Date: 2018/10/26 11:31
     *@Description :  微信报件
     *@Params :  * @param null
     */
    private String createAliOrWxReport(String channelType, String terMerNo, BusiAccount busiAccount) throws Exception {
        String merNo = busiAccount.getMerNo();
        String merName = busiAccount.getBusiName();
        String contactName = busiAccount.getContactName();
        String cerdId = busiAccount.getCerdId();
        String contactPhone = busiAccount.getContactPhone();
        String address = busiAccount.getAddress();
        String appletsAppId = busiAccount.getAppletsAppId();

        String servicePhone = busiAccount.getServicePhone();
        String contactEmail = busiAccount.getContactEmail();
        String categoryId = null;
        if (channelType.equals("WEIXIN")) {
            categoryId = busiAccount.getWxProfCode();
        } else {
            categoryId = busiAccount.getAliProfCode();
        }
        String merNameAlias = busiAccount.getMerNameAlias();
        String note = "N" + IDUtil.getID();

        Instance instance = busiAccount.getInstance();
        InstanceExpandUnion expandUnion = instanceExpandUnionDao.findByInstanceId(instance.getInstanceId());
        String acqPubKey = expandUnion.getAcqPubKey();
        String acqPrvKey = expandUnion.getAcqPrvKey();

        DefaultHttpClient httpClient = new SSLClient();
        HttpPost postMethod = new HttpPost(constants.getUnionTransUrl() + "/backTransReq");
        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("transId", "52"));//交易类型
        nvps.add(new BasicNameValuePair("chnlType", channelType));//渠道类型微信：WEIXIN 支付宝：ALIPAY
        nvps.add(new BasicNameValuePair("requestNo", IDUtil.getID()));//请求流水号
        nvps.add(new BasicNameValuePair("merNo", merNo));//机构商户号
        if (StringUtils.isNotEmpty(appletsAppId) ){
            nvps.add(new BasicNameValuePair("appid", appletsAppId));//支付宝服务窗、微信小程序交易必输，其他产品不传
        }
        nvps.add(new BasicNameValuePair("terMerNo", terMerNo));//终端商户号
        nvps.add(new BasicNameValuePair("merName", merName));//商户名称
        nvps.add(new BasicNameValuePair("merNameAlias", merNameAlias));//商户简称
        nvps.add(new BasicNameValuePair("contactName", contactName));//联系人
        nvps.add(new BasicNameValuePair("cerdId", cerdId));//身份证
        nvps.add(new BasicNameValuePair("contactPhone", contactPhone));//联系电话
        nvps.add(new BasicNameValuePair("contactEmail", contactEmail));//联系人邮箱
        nvps.add(new BasicNameValuePair("servicePhone", servicePhone));//客服电话
        nvps.add(new BasicNameValuePair("address", address));//地址
        nvps.add(new BasicNameValuePair("categoryId", categoryId));//行业类别
        nvps.add(new BasicNameValuePair("note", note));//备注
        nvps.add(new BasicNameValuePair("signature", SignTermUtil.signData(nvps, acqPrvKey)));//签名
        postMethod.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        HttpResponse resp = httpClient.execute(postMethod);
        String str = EntityUtils.toString(resp.getEntity(), "UTF-8");
        int statusCode = resp.getStatusLine().getStatusCode();
        if (200 == statusCode) {
            Map<String, String> responseMap = SignTermUtil.verferSignData(str, acqPubKey);
            String bankSpCode = responseMap.get("bankSpCode");//子商户号  可查看审核状况
            return bankSpCode;
        } else {
            ValueUtil.isError("报件失败，请重新报件");
        }
        System.out.println("返回错误码:" + statusCode);
        return null;
    }

    /*
     *@Author : Gavin
     *@Email : gavinsjq@sina.com
     *@Date: 2018/10/26 10:50
     *@Description : 终端报件
     *@Params :  * @param null
     */
    private String terminalReport(BusiAccount busiAccount, String channelType) throws Exception {
        System.out.println("attr1========" + busiAccount.getAttr1());
        String merNo = busiAccount.getMerNo();
        String merName = busiAccount.getBusiName();
        String contactName = busiAccount.getContactName();
        String cerdId = busiAccount.getCerdId();
        String contactPhone = busiAccount.getContactPhone();
        String provinceId = busiAccount.getProvinceId();
        String cityId = busiAccount.getCityId();
        String cardNoCipher = busiAccount.getCardNoCipher();
        String address = busiAccount.getAddress();
        String accBankNo = busiAccount.getAccBankNo();
        String cardName = busiAccount.getCardName();
        String isCompay = busiAccount.getIsCompay();

        Instance instance = busiAccount.getInstance();
        InstanceExpandUnion expandUnion = instanceExpandUnionDao.findByInstanceId(instance.getInstanceId());
        String acqPubKey = expandUnion.getAcqPubKey();
        String acqPrvKey = expandUnion.getAcqPrvKey();

        Map<String, String> textParam = new HashMap<String, String>();
        //定值
        textParam.put("version", "V1.0");  //版本号
        textParam.put("transId", "28"); //交易类型
        textParam.put("settleToType", "BANKCARD");  //结算方式  固定值: BANKCARD
        textParam.put("legalIdType", "IdCard"); //法人证件类型 IdCard表示身份证

        String requestNo = IDUtil.getID();
        textParam.put("requestNo", requestNo);  //请求流水号
        textParam.put("merNo", expandUnion.getUnionMerId()); //机构商户号  申请商户入网后，由收单机构发放

        textParam.put("terMerName", merName);  //终端商户名称  终端商户唯一标识
        textParam.put("address", address);  //终端商户所在详细地址
        textParam.put("legalPerson", contactName); //法人名称
        textParam.put("legalIdNo", cerdId);   //法人证件号码
        textParam.put("legalMobilePhone", contactPhone);   //法人手机号

        textParam.put("province", provinceId);    //终端商户所在省份，上送地区ID，如广东省，上送30
        textParam.put("city", cityId);    //终端商户所在城市，上送地区ID，如深圳市，上送44
        textParam.put("cardNoCipher", cardNoCipher); //结算账号   即银行卡号
        textParam.put("accBankNo", accBankNo); //联行号 结算账号所属联行号
        textParam.put("isCompay", isCompay); //是否对公账户  0：否1：是  ？？有什么区别
        textParam.put("cardName", cardName);  //结算账号户名

        //查看当前实例下有支付渠道有哪些及相应的支付费率是多少
        List<PaymentChannel> paymentChannelList = channelService.findByInstanceId(instance.getInstanceId());
        JSONArray productAndRateArr = new JSONArray(paymentChannelList.size());
        for (PaymentChannel paymentChannel : paymentChannelList) {
            JSONObject productAndRateObj = new JSONObject();
            productAndRateObj.put("product", paymentChannel.getUnionProdId());
            productAndRateObj.put("rate", "[" + paymentChannel.getProRate() + "]");
            productAndRateObj.put("template", paymentChannel.getUnionRateType());
            productAndRateArr.add(productAndRateObj);
        }

        textParam.put("productAndRate", productAndRateArr.toJSONString());//  产品和费率模板json字符串

        Map<String, Object> fileParams = new HashMap<String, Object>();
        byte[] byte1 = new ByteArrayOutputStream() {
            @Override
            public synchronized byte[] toByteArray() {
                try {
                    InputStream a1 = busiAccount.getAttr1().getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int bytesRead = 0;
                    byte[] buffer = new byte[8192];
                    while ((bytesRead = a1.read(buffer, 0, 8192)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                    }

                    a1.close();
                    return bos.toByteArray();
                } catch (Exception e) {
                }
                return null;
            }
        }.toByteArray();

        byte[] byte2 = new ByteArrayOutputStream() {
            @Override
            public synchronized byte[] toByteArray() {
                try {
                    InputStream a1 = busiAccount.getAttr2().getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int bytesRead = 0;
                    byte[] buffer = new byte[8192];
                    while ((bytesRead = a1.read(buffer, 0, 8192)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                    }

                    a1.close();
                    return bos.toByteArray();
                } catch (Exception e) {
                }
                return null;
            }
        }.toByteArray();
        byte[] byte3 = new ByteArrayOutputStream() {
            @Override
            public synchronized byte[] toByteArray() {
                try {
                    InputStream a1 = busiAccount.getAttr3().getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int bytesRead = 0;
                    byte[] buffer = new byte[8192];
                    while ((bytesRead = a1.read(buffer, 0, 8192)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                    }

                    a1.close();
                    return bos.toByteArray();
                } catch (Exception e) {
                }
                return null;
            }
        }.toByteArray();
        byte[] byte4 = new ByteArrayOutputStream() {
            @Override
            public synchronized byte[] toByteArray() {
                try {
                    InputStream a1 = busiAccount.getAttr4().getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int bytesRead = 0;
                    byte[] buffer = new byte[8192];
                    while ((bytesRead = a1.read(buffer, 0, 8192)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                    }

                    a1.close();
                    return bos.toByteArray();
                } catch (Exception e) {
                }
                return null;
            }
        }.toByteArray();
        fileParams.put("attachment1", byte1);
        fileParams.put("attachment2", byte2);
        fileParams.put("attachment3", byte3);
        fileParams.put("attachment4", byte4);
        System.out.println("byte1111111=========" + byte1);

        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        Iterator<Map.Entry<String, String>> iterator = textParam.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            String key = next.getKey();
            String value = next.getValue();
            nvps.add(new BasicNameValuePair(key, value));
        }
        textParam.put("signature", SignTermUtil.signData(nvps, acqPrvKey));
        busiAccount.setOriRequestNo(requestNo);
        String conHttp = HttpClient.conHttp(constants.getUnionTransUrl() + "/applyTranRequest", textParam, fileParams, null);
        Map<String, String> responseMap = SignTermUtil.verferSignData(conHttp, acqPubKey);

        String respCode = responseMap.get("respCode");
        if (!respCode.equals("0000") && !respCode.equals("00")) {
            String respMsg = responseMap.get("respDesc");
            ValueUtil.isError(respMsg);
        }

        ResourcePic pic1 = new ResourcePic();
        pic1.setResourceId(busiAccount.getId());
        pic1.setContent(Base64Utils.encodeToString(byte1));
        ResourcePic pic2 = new ResourcePic();
        pic2.setResourceId(busiAccount.getId());
        pic2.setContent(Base64Utils.encodeToString(byte2));
        ResourcePic pic3 = new ResourcePic();
        pic3.setResourceId(busiAccount.getId());
        pic3.setContent(Base64Utils.encodeToString(byte3));
        ResourcePic pic4 = new ResourcePic();
        pic4.setResourceId(busiAccount.getId());
        pic4.setContent(Base64Utils.encodeToString(byte4));
        resourcePicDao.save(pic1);
        resourcePicDao.save(pic2);
        resourcePicDao.save(pic3);
        resourcePicDao.save(pic4);

        String terMerNo = responseMap.get("terMerNo");
        return terMerNo;
    }

    @Override
    public String ocrIdCardAuth(Map<String, Object> params) {
        ValueUtil.verifyParams("frontImage,cardSide,isInHand,cardInfoCallback", params.get("frontImage"), params.get("cardSide"), params.get("isInHand"), params.get("cardInfoCallback"));
        if ("both".equals(params.get("cardSide")) && Objects.isNull(params.get("backImage"))) {
            throw new CustomException("720", "后台校验参数数量错误");
        }
        Map<String, String> map = new HashMap<String, String>(6);
        //必填ture string 身份证正⾯base64数据
        map.put("frontImage", map.get("frontImage"));
        //必填false string 身份证反⾯base64数据，cardSide为both时必选
        map.put("backImage", map.get("backImage"));
        //true string 身份证正反⾯，front：正⾯； both：正反⾯
        map.put("cardSide", map.get("cardSide"));
        //必填ture string 是否为⼿持身份证，1：是； 0：不是
        map.put("isInHand", map.get("isInHand"));
        //必填ture string 是否返回身份证信息 1：返回； 0：不返回
        map.put("cardInfoCallback", map.get("cardInfoCallback"));
        //必填ture string 接⼝令牌，由平台分配 固定不变的
        map.put("token", ocrToken);
        try {
            //		{"code":"00","msg":"成功","data":{"cardInfo":{"birthday":"19910216","address":"河南省睢县匡城乡武唐村125号","nationality":"汉","sex":"男","name":"唐东峰","signOffice":"睢县公安局","signDate":"20180212","idNum":"411422199102162499","invalidDate":"20380212"},"respInfo":"合同已过期或未签订合同","respCode":"51"}}
            //		{"code":"00","msg":"成功","data":{"respInfo":"合同已过期或未签订合同","respCode":"51"}}
            return ValueUtil.toJson(JSONObject.parseObject(HttpUtil.post(ocrUrl, map).getResponseText()).get("data"));
        } catch (Exception e) {
            e.printStackTrace();
            return ValueUtil.toError("500", e.getMessage());
        }
    }

    @Override
    public Object subBusiCata(BusiAccount busiAccount) throws Exception {
        ValueUtil.verifyParams("paymentPath", busiAccount);
        DefaultHttpClient httpClient = new SSLClient();
        HttpPost postMethod = new HttpPost(constants.getUnionTransUrl() + "/backTransReq");
        BusiAccount dbBusiAccount = busiAccountService.findByInstanceIdAndNumber(busiAccount.getInstanceId(), busiAccount.getNumber());
        Instance instance = busiAccount.getInstance();
        InstanceExpandUnion expandUnion = instanceExpandUnionDao.findByInstanceId(dbBusiAccount.getInstanceId());

        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("requestNo", IDUtil.getID()));//请求流水号
        nvps.add(new BasicNameValuePair("transId", "53"));//交易类型
        nvps.add(new BasicNameValuePair("merNo", dbBusiAccount.getMerNo()));//终端商户号
        nvps.add(new BasicNameValuePair("bankSpCode", dbBusiAccount.getWxBankSpCode()));//微信子商户号
        nvps.add(new BasicNameValuePair("jsapiPath", busiAccount.getPaymentPath()));//商户名称
        nvps.add(new BasicNameValuePair("configType", "path"));//商户简称

        nvps.add(new BasicNameValuePair("signature", SignTermUtil.signData(nvps, expandUnion.getAcqPrvKey())));//签名
        postMethod.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        HttpResponse resp = httpClient.execute(postMethod);
        String str = EntityUtils.toString(resp.getEntity(), "UTF-8");
        int statusCode = resp.getStatusLine().getStatusCode();
        if (200 == statusCode) {
            Map<String, String> responseMap = SignTermUtil.verferSignData(str, expandUnion.getAcqPubKey());
            String respCode = responseMap.get("respCode");
            if (!respCode.equals("0000") && !respCode.equals("00")) {
                String respMsg = responseMap.get("respDesc");
                ValueUtil.isError(respMsg);
            }
        } else {
            ValueUtil.isError("配置失败，请重新报件");
        }
        return null;
    }
}
