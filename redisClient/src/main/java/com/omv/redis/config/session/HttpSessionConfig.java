package com.omv.redis.config.session;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

/**
 * Created by by on 2017/12/22.
 * 请求接口后  在Response Header中包含 x-auth-token:dd3f5124-deaa-4a0f-be45-9ef128d0ef20
 * 在之后请求的接口中  需要在Request Header 中 添加 x-auth-token:dd3f5124-deaa-4a0f-be45-9ef128d0ef20 请求头
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 100)
public class HttpSessionConfig {
    @Bean
    public HttpSessionIdResolver httpSessionStrategy() {
        return new HeaderHttpSessionIdResolver("X-Auth-Token");
    }
}
