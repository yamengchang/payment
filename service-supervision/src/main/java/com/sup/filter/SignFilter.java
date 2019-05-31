package com.sup.filter;

import com.omv.common.util.basic.MapUtil;
import com.omv.common.util.basic.ValueUtil;
import com.omv.common.util.log.LoggerUtils;
import com.omv.common.util.signature.EncryptManager;
import com.omv.common.util.signature.SignUtils;
import com.sup.remote.PayRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zwj on 2019/1/8.
 */
@Component
public class SignFilter implements Filter {
    @Autowired
    private PayRemote payRemote;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        ServletRequest requestWrapper = null;
        if (servletRequest instanceof HttpServletRequest) {
            requestWrapper = new BodyReaderHttpServletRequestWrapper((HttpServletRequest) servletRequest);
        } else {
            requestWrapper = servletRequest;
        }
        HttpServletRequest request = (HttpServletRequest) requestWrapper;
        String uri = request.getRequestURI();
        if (uri.indexOf("scrt") >= 0) {
            checkRequestBodyParams(request);
        }
        chain.doFilter(request, servletResponse);
        return;
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

        String instanceInfo = payRemote.instanceInfo(instanceId);
        String publicKey = ValueUtil.getFromJson(instanceInfo, "data", "content", "publicKey");
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
