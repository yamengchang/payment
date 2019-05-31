package com.pay.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by MING on 2018/11/9.
 * Description:
 */
public interface SignatureService {

    void authorization() throws Exception;

    String certificateEnterprise(Map<String, String> params) throws Exception;

    String certificatePerson(Map<String, String> params) throws Exception;

    JSONArray start(Map<String, Object> params) throws Exception;

    Object query(String orderId, String instanceId, Integer page, Integer rows) throws Exception;

    String download(String orderId, String type, String instanceId, HttpServletResponse response) throws Exception;

    JSONArray templateList(Map<String, String> params) throws Exception;

    String templateView(String templateNo, HttpServletResponse response) throws Exception;
}
