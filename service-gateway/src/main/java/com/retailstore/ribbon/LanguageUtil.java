package com.retailstore.ribbon;

import org.apache.commons.lang.*;

import javax.servlet.http.*;

/**
 * Created by zwj on 2018/3/20.
 */

public class LanguageUtil {
    public static String getLanguage() {
        try {
            HttpServletRequest request = HttpServletUtil.getRequests();

//            if(request == null||"1".equals(HttpServletUtil.getIsGetCustomLanguage())){
            if (request == null) {
                String language = HttpServletUtil.getCustomHeader("language");
                if (language == null) {
                    return "zh-CN";
                }
                return language;
            } else {
                String requestHeaderLanguage = request.getHeader("language");
                if (requestHeaderLanguage != null && !requestHeaderLanguage.equals("undefind")) {
                    return requestHeaderLanguage;
                } else {
                    return "zh-CN";
                }
            }
        } catch (Exception e) {
            return "zh-CN";
        }

    }

    public static boolean checkChinese() {
        if (!getLanguage().equals("zh-CN")) {
            return false;
        } else {
            return true;
        }
    }

    public static void changeLanguage(String language) {
        if (StringUtils.isNotEmpty(language)) {
            HttpServletUtil.putCustomHeader("language", language);
        }
    }
}
