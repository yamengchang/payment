package com.omv.common.util.fegin;

import com.omv.common.util.basic.HttpServletUtil;
import com.omv.common.util.basic.LanguageUtil;
import com.omv.common.util.log.LoggerUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.EventListener;

/**
 * Created by zwj on 2018/3/20.
 */
@Component
@Order(101)
public class FeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {

        try {
            requestTemplate.header("initiator","feign");
            HttpServletRequest request =  HttpServletUtil.getRequests();
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    String values = request.getHeader(name);
                    requestTemplate.header(name, values);
                }
            }
        } catch (Exception e) {
            LoggerUtils.error("获取request失败");
        }
    }
}
