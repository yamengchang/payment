package com.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.omv.common.util.basic.IDUtil;
import com.omv.common.util.basic.ValueUtil;
import com.pay.dao.SignatureRepository;
import com.pay.entity.Signature;
import com.pay.service.SignatureService;
import com.pay.utils.DateUtil;
import com.pay.utils.https.HttpsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by MING on 2018/11/9.
 * Description:
 */
@EnableScheduling
@Service
public class SignatureServiceImpl implements SignatureService {

    private Logger logger = LoggerFactory.getLogger(SignatureServiceImpl.class);

    private static String AUTHORIZATION_CODE;

    private final SignatureRepository signatureRepository;

    @Value("${signature_authorization}")
    private String SIGNATURE_AUTHORIZATION;//授权url
    @Value("${signature_certificate_ent}")
    private String SIGNATURE_CERTIFICATE_ENT;//企业证书颁发url
    @Value("${signature_certificate_per}")
    private String SIGNATURE_CERTIFICATE_PER;//个人证书颁发url
    @Value("${signature_start}")
    private String SIGNATURE_START;//发起签约
    @Value("${signature_query}")
    private String SIGNATURE_QUERY;//签约查询
    @Value("${signature_download}")
    private String SIGNATURE_DOWNLOAD;//签约下载
    @Value("${signature_template_list}")
    private String SIGNATURE_TEMPLATE_LIST;//模板列表
    @Value("${signature_template_view}")
    private String SIGNATURE_TEMPLATE_VIEW;//模板预览

    @Autowired
    public SignatureServiceImpl(SignatureRepository signatureRepository) {
        this.signatureRepository = signatureRepository;
    }

    @Override
    public void authorization() throws Exception {
        String method = "POST";
        Map<String, String> params = new HashMap<>();
        params.put("client_id", "usign_client_0001");
        params.put("client_secret", "1d7dede1db41815f8d7359480bf873a6");
        params.put("response_type", "token");
        params.put("grant_type", "client_credentials");
        params.put("provision_key", "7279ef8e28c09eeed6c88f46853b6a90");
        logger.info("授权链接：" + SIGNATURE_AUTHORIZATION);
        logger.info("授权参数：" + new Gson().toJson(params));
        String res = HttpsService.sendRequestNoCheckCerPostMap(SIGNATURE_AUTHORIZATION, method, null, params);
        JSONObject result = JSONObject.parseObject(res);
        String token_type = result.getString("token_type");
        String access_token = result.getString("access_token");
//        String expires_in = result.getString("expires_in");
        AUTHORIZATION_CODE = token_type + " " + access_token;
        logger.info("授权返回结果：" + new Gson().toJson(res));
        System.out.println(result);
    }

    @PostConstruct
    public void auto1() {
        try {
            authorization();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void auto2() {
        try {
            authorization();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String certificateEnterprise(Map<String, String> params) throws Exception {
        ValueUtil.verifyParams("businessLicenseRegNo,legalRepreNm,registeredAddr,comNm",
                params.get("businessLicenseRegNo"), params.get("legalRepreNm"),
                params.get("registeredAddr"), params.get("comNm"));
        String method = "POST";
        Map<String, String> header = new HashMap<>();
        header.put("auth", AUTHORIZATION_CODE);
//        params.put("signature","");
        logger.info("企业证书颁发链接：" + SIGNATURE_CERTIFICATE_ENT);
        logger.info("企业证书颁发参数：" + new Gson().toJson(params));
        String res = HttpsService.sendRequestNoCheckCerPostMap(SIGNATURE_CERTIFICATE_ENT, method, header, params);
//        JSONObject result = JSONObject.parseObject(res);
        logger.info("企业证书颁发返回结果：" + new Gson().toJson(res));
        try {
            assert res != null;
            return ValueUtil.getFromJson(res, "data", "userId");
        } catch (Exception ex) {
            String msg = ValueUtil.getFromJson(res, "msg");
            ValueUtil.isError(msg);
            return null;
        }
    }

    @Override
    public String certificatePerson(Map<String, String> params) throws Exception {
        ValueUtil.verifyParams("personName,identNo,accNo,mobilePhone",
                params.get("personName"), params.get("identNo"),
                params.get("accNo"), params.get("mobilePhone"));
        String method = "POST";
        Map<String, String> header = new HashMap<>();
        header.put("auth", AUTHORIZATION_CODE);
//        params.put("signature","");
        logger.info("个人证书颁发链接：" + SIGNATURE_CERTIFICATE_PER);
        logger.info("个人证书颁发参数：" + new Gson().toJson(params));
        String res = HttpsService.sendRequestNoCheckCerPostMap(SIGNATURE_CERTIFICATE_PER, method, header, params);
//        JSONObject result = JSONObject.parseObject(res);
        logger.info("个人证书颁发返回结果：" + new Gson().toJson(res));
        try {
            assert res != null;
            return ValueUtil.getFromJson(res, "data", "userId");
        } catch (Exception ex) {
            String msg = ValueUtil.getFromJson(res, "msg");
            ValueUtil.isError(msg);
            return null;
        }
    }

    @Override
    public JSONArray start(Map<String, Object> params) throws Exception {
        ValueUtil.verifyParams("userId,personName,identNo,mobilePhone",
                params.get("userId"), params.get("personName"),
                params.get("identNo"), params.get("mobilePhone"));
        String method = "POST";
        String order = IDUtil.getID();
        String insCode = "10010001";
        String invokeTimestamp = DateUtil.formatTimeStamp(new Date());
        Map<String, String> header = new HashMap<>();
        header.put("auth", AUTHORIZATION_CODE);
        Map<String, String> param = new HashMap<>();
        param.put("insCode", "10010001");
        param.put("templateNo", (String) params.get("templateNo"));
        param.put("signNo", order);
        param.put("invokeTimestamp", invokeTimestamp);
        Map<String, String> con1 = new HashMap<>();
        con1.put("userId", (String) params.get("userId"));
        Map<String, String> con2 = new HashMap<>();
        con2.put("personName", (String) params.get("personName"));
        con2.put("identNo", (String) params.get("identNo"));
        con2.put("mobilePhone", (String) params.get("mobilePhone"));
        List<Map<String, String>> con = new ArrayList<>();
        con.add(con1);
        con.add(con2);
        params.put("party", con);
        params.remove("userId");
        params.remove("personName");
        params.remove("identNo");
        params.remove("mobilePhone");
        param.put("contractElement", new Gson().toJson(params));
        logger.info("发起签约链接：" + SIGNATURE_START);
        logger.info("发起签约参数：" + new Gson().toJson(param));
        //TODO 只判断成功的orderId，不判断重复
        List<Signature> signatureList = signatureRepository.findAllByOrderIdAndInstanceIdAndResultOrderByCreateTimeDesc((String) params.get("orderId"), (String) params.get("instanceId"), "0");
        if (!signatureList.isEmpty()) {
            ValueUtil.isError("订单号不能重复");
        }
        Signature signature = new Signature();
        signature.setId(order);
        signature.setOrderId((String) params.get("orderId"));
        signature.setInstanceId((String) params.get("instanceId"));
        signature.setInsCode(insCode);
        signature.setTemplateNo((String) params.get("templateNo"));
        signature.setSignNo(order);
        signature.setInvokeTimestamp(invokeTimestamp);
        signature.setContractElement(new Gson().toJson(params));
        signatureRepository.save(signature);
        String res = HttpsService.sendRequestNoCheckCerPostMap(SIGNATURE_START, method, header, param);
//        JSONObject result = JSONObject.parseObject(res);
        logger.info("发起签约返回结果：" + new Gson().toJson(res));
        try {
            assert res != null;
            signature.setResultMsg(ValueUtil.getFromJson(res, "data", "party"));
            signatureRepository.save(signature);
            return JSON.parseArray(ValueUtil.getFromJson(res, "data", "party"));
        } catch (Exception ex) {
            String msg = ValueUtil.getFromJson(res, "msg");
            ValueUtil.isError(msg);
            return null;
        }
    }

    @Override
    public Object query(String orderId, String instanceId, Integer page, Integer rows) throws Exception {
        ValueUtil.verifyParams("orderId", orderId);
        //TODO 只判断成功的orderId，不判断重复
        List<Signature> signatureList = signatureRepository.findAllByOrderIdAndInstanceIdAndResultOrderByCreateTimeDesc(orderId, instanceId, "0");
        if (!signatureList.isEmpty()) {
            Signature signature = signatureList.get(0);
            return signature.getResultMsg();
        }
        signatureList = signatureRepository.findAllByOrderIdAndInstanceIdAndResultOrderByCreateTimeDesc(orderId, instanceId, "1");
        Signature signature = signatureList.get(0);
        Map<String, String> params = new HashMap<>();
        params.put("signNo", signature.getSignNo());
        if (page != null) params.put("page", String.valueOf(page));
        if (rows != null) params.put("rows", String.valueOf(rows));
        String method = "POST";
        Map<String, String> header = new HashMap<>();
        header.put("auth", AUTHORIZATION_CODE);
//        params.put("signature","");
        logger.info("签约查询链接：" + SIGNATURE_QUERY);
        logger.info("签约查询参数：" + new Gson().toJson(params));
        String res = HttpsService.sendRequestNoCheckCerPostMap(SIGNATURE_QUERY, method, header, params);
//        JSONObject result = JSONObject.parseObject(res);
        logger.info("签约查询返回结果：" + new Gson().toJson(res));
        try {
            assert res != null;
            String json = ValueUtil.getFromJson(res, "data");
            signature.setResultMsg(json);
            Map map = new Gson().fromJson(json, Map.class);
            if (map.get("signmentStateA") != null && map.get("signmentStateB") != null) {
                signature.setResult("0");
            }
            signatureRepository.save(signature);
            return JSON.parseObject(json);
        } catch (Exception ex) {
            String msg = ValueUtil.getFromJson(res, "msg");
            ValueUtil.isError(msg);
            return null;
        }
    }

    @Override
    public String download(String orderId, String type, String instanceId, HttpServletResponse response) throws Exception {
        ValueUtil.verifyParams("orderId,type", orderId, type);
        List<Signature> signatureList = signatureRepository.findAllByOrderIdAndInstanceIdAndResultOrderByCreateTimeDesc(orderId, instanceId, "0");
        if (signatureList.isEmpty()) {
            ValueUtil.isError("订单号错误");
        }
        Signature signature = signatureList.get(0);
        String method = "POST";
        Map<String, String> params = new HashMap<>();
        params.put("signNo", signature.getSignNo());
        params.put("type", type);
        Map<String, String> header = new HashMap<>();
        header.put("auth", AUTHORIZATION_CODE);
//        params.put("signature","");
        logger.info("签约下载链接：" + SIGNATURE_DOWNLOAD);
        logger.info("签约下载参数：" + new Gson().toJson(params));
        String res = HttpsService.sendRequestNoCheckCerPostMap(SIGNATURE_DOWNLOAD, method, header, params);
        JSONObject result = JSONObject.parseObject(res);
        logger.info("签约下载返回结果：" + result);
        String status = result.getString("status");
        if (!"00".equals(status)) return res;
        JSONObject data = result.getJSONObject("data");
        String fileName = data.getString("name");
        String content = data.getString("content");
        switch (type) {
            case "jpg":
                if (content.contains("data:image/jpg;base64,")) {
                    return res;
                } else {
                    content = "data:image/jpg;base64," + content;
                    result.remove("content");
                    result.put("content", content);
                    return result.toJSONString();
                }
            case "pdf":
                // 读到流中
                InputStream inStream = new ByteArrayInputStream((new sun.misc.BASE64Decoder()).decodeBuffer(content));
                OutputStream os = new BufferedOutputStream(response.getOutputStream());
                // 设置输出的格式
                response.reset();
                response.setContentType("application/x-msdownload");
                response.addHeader("Content-Disposition", "attachment; filename=\"" +
                        new String((fileName + ".pdf").getBytes(StandardCharsets.UTF_8),
                                "iso8859-1") + "\";charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                //一次读出10兆
                byte[] b = new byte[1024 * 1024 * 10];
                int len;
                while ((len = inStream.read(b)) > 0) {
                    os.write(b, 0, len);
                }
                inStream.close();
                os.flush();
                os.close();
                return null;
            default:
                return res;
        }
    }

    @Override
    public JSONArray templateList(Map<String, String> params) throws Exception {
        String page = params.get("page");
        String rows = params.get("rows");
        if (page == null || "".equals(page)) {
            page = "1";
            params.put("page", page);
        }
        if (rows == null || "".equals(rows)) {
            rows = "10";
            params.put("rows", rows);
        }
        String method = "POST";
        Map<String, String> header = new HashMap<>();
        header.put("auth", AUTHORIZATION_CODE);
//        params.put("signature","");
        logger.info("签约查询链接：" + SIGNATURE_TEMPLATE_LIST);
        logger.info("签约查询参数：" + new Gson().toJson(params));
        String res = HttpsService.sendRequestNoCheckCerPostMap(SIGNATURE_TEMPLATE_LIST, method, header, params);
//        JSONObject result = JSONObject.parseObject(res);
        logger.info("签约查询返回结果：" + new Gson().toJson(res));
        try {
            assert res != null;
            String json = ValueUtil.getFromJson(res, "data");
            return JSON.parseArray(json);
        } catch (Exception ex) {
            String msg = ValueUtil.getFromJson(res, "msg");
            ValueUtil.isError(msg);
            return null;
        }
    }

    @Override
    public String templateView(String templateNo, HttpServletResponse response) throws Exception {
        ValueUtil.verifyParams("templateNo", templateNo);
        String method = "GET";
        Map<String, String> header = new HashMap<>();
        header.put("auth", AUTHORIZATION_CODE);
//        params.put("signature","");
        logger.info("签约下载链接：" + SIGNATURE_TEMPLATE_VIEW + templateNo);
//        logger.info("签约下载参数：" + new Gson().toJson(params));
        HttpsService.sendRequestNoCheckCerPostMapInputStream(SIGNATURE_TEMPLATE_VIEW + templateNo, method, header, null, response, templateNo);
        return null;
    }

}
