package com.pay.filter;

import com.omv.common.util.basic.MapUtil;
import com.omv.common.util.basic.ValueUtil;
import com.omv.common.util.httpclient.bean.RequestMethod;
import com.omv.common.util.log.LoggerUtils;
import com.omv.common.util.signature.SignUtils;
import com.pay.bean.Constants;
import com.pay.entity.Instance;
import com.pay.service.InstanceService;
import com.pay.utils.BodyReaderHttpServletRequestWrapper;
import com.pay.utils.union.sdk.LogUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * 用于过滤请求中的实例相关参数是否合法
 * */
//@Component
public class InstanceFilter implements Filter {

    @Autowired
    private InstanceService instanceService;
    @Autowired
    private Constants constants;


    private MultipartResolver multipartResolver;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        ServletRequest requestWrapper = null;
//        if (servletRequest instanceof HttpServletRequest) {
//            requestWrapper = new BodyReaderHttpServletRequestWrapper((HttpServletRequest) servletRequest);
//        } else {
            requestWrapper = servletRequest;
//        }
        HttpServletRequest request = (HttpServletRequest) requestWrapper;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String url = request.getRequestURI();
        String method = request.getMethod();
        String referer = request.getHeader("Referer");
        if (method.equals(RequestMethod.post.name().toUpperCase()) && StringUtils.contains(url, "scrt")) {
            if (referer != null) {
                referer = referer.split("\\?")[0];
                System.out.println("========= referer ==========: " + referer);
                if (referer.equals(constants.getPAYMENT_DOMAIN() + "/scrt/polymeric/trade")) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }

            String instanceId = request.getParameter("instanceId");
            String contentType = request.getContentType();
            if (StringUtils.isEmpty(instanceId) && contentType.contains("application/json")) {
                checkRequestBodyParams(request);
                filterChain.doFilter(request, response);
                return;
            }else if(StringUtils.isEmpty(instanceId) && contentType.contains("multipart/form-data")){
                CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
                MultipartHttpServletRequest multipartRequest = commonsMultipartResolver.resolveMultipart(request);
                checkRequestMultParams(multipartRequest);

            } else {
                checkRequestParameterParams(request);
                filterChain.doFilter(request, response);
                return;
            }
        }
        filterChain.doFilter(request, response);
        return;
    }

    private void checkRequestMultParams(MultipartHttpServletRequest request) {
        String instanceId = request.getParameter("instanceId");
        Enumeration<String> enumeration = request.getParameterNames();
        Map<String, String> reqMap = new HashMap<>();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            String value = request.getParameter(key);
            reqMap.put(key, value);
        }
        System.out.println(reqMap.toString());
    }

    private void checkRequestParameterParams(HttpServletRequest request) {
        String instanceId = request.getParameter("instanceId");
        Enumeration<String> enumeration = request.getParameterNames();
        Map<String, String> reqMap = new HashMap<>();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            String value = request.getParameter(key);
            reqMap.put(key, value);
        }
        Instance instance = instanceService.findByInstanceId(instanceId);
        validateInstance(instance);

        String publicKey = instance.getPublicKey();
//        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCwepE/AC3q91tnfflsIlOtO72/VBGtAkCdwLHb5obRG6zfgJPKTQdq9RGVtzTggMo6+fqtLElrEisiM2nAYdQCQMkXFRjx5Pp/Vj4WxDcAoY7EsrcqA4ZbKkNa15Y8QY9/u0IY7uuIegr8/KiqAjKyqUkOXd+DnvJ4/FFoKeNNSQIDAQAB";
        LoggerUtils.info(reqMap.toString());
        if (!SignUtils.verifySign(reqMap, publicKey)) {
            ValueUtil.isError("实例/应用验签失败");
        }
    }

    private void validateInstance(Instance instance) {
        if (instance == null) {
            ValueUtil.isError("该实例/应用不存在！");
        }
        if (!instance.getStatus().equals(Constants.INSTANCE_USING)) {
            ValueUtil.isError("该实例/应用尚未审核成功，赞无法使用");
        }
    }

    private void checkRequestBodyParams(HttpServletRequest request) throws IOException {
        request = new BodyReaderHttpServletRequestWrapper(request);  //替换
        Map<String, Object> bodyParams = getBodyParams(request);
        String instanceId = null;
        Map<String, String> reqMap = new LinkedHashMap();
        for (Map.Entry<String, Object> entry : bodyParams.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key.equals("instanceId")) {
                instanceId = (String) value;
            }
            reqMap.put(key, value.toString());
        }
        Instance instance = instanceService.findByInstanceId(instanceId);
        validateInstance(instance);
        String publicKey = instance.getPublicKey();

        LoggerUtils.info(reqMap.toString());
        if (!SignUtils.verifySign(reqMap, publicKey)) {
            ValueUtil.isError("验签失败");
        }
    }

    private Map<String, Object> getBodyParams(HttpServletRequest request) throws IOException {
        BufferedReader br = request.getReader();
        String str, wholeStr = "";
        while ((str = br.readLine()) != null) {
            wholeStr += str;
        }
        return MapUtil.str2Map(wholeStr);
    }

    @Override
    public void destroy() {

    }

}
