package com.eureka;

//import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zwj on 2018/3/26.
 */
@Configuration
//@EnableApolloConfig
@EnableEurekaServer
@SpringBootApplication
public class RegisteCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(RegisteCenterApplication.class, args);
    }
}
