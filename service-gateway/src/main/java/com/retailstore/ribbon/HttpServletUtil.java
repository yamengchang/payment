package com.retailstore.ribbon;

import org.springframework.web.context.request.*;

import javax.servlet.http.*;
import java.util.*;

/**
 * Created by by on 2017/10/30.
 */
public class HttpServletUtil extends HttpServletRequestWrapper {
    private static Map<String, Object> customHeaders = new HashMap<>();

    public HttpServletUtil(HttpServletRequest request) {
        super(request);
    }

    public static void putCustomHeader(String name, String value) {
        customHeaders.put(name, value);
    }

    public static void putIsGetCustomLanguage(String value) {
        customHeaders.put("isCustomLanguage", value == null ? "0" : value);
    }

    public static String getIsGetCustomLanguage() {
        return (customHeaders.get("isCustomLanguage") == null ? "0" : customHeaders.get("isCustomLanguage").toString());
    }

    public static String getCustomHeader(String name) {
        return customHeaders.get(name).toString();
    }

    public static HttpServletRequest getRequests() {
        try {
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attr.getRequest();
            return request;
        } catch (IllegalStateException e) {
            return null;
        }
    }

    public static HttpServletResponse getResponse() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletResponse response = attr.getResponse();
        return response;
    }

}
