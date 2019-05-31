package com.pay;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by zwj on 2018/9/26.
 */
//springboot 启动类 DataSourceAutoConfiguration.class,
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
//声明eureka客户端
@EnableDiscoveryClient
//声明 feign 开启远程调用
@EnableFeignClients
@EnableAutoConfiguration
@ComponentScan(value = {"com"})
@Controller
@EnableSwagger2
@EnableScheduling
public class PayApplication implements WebMvcConfigurer {
	public static void main(String[] args) {
		SpringApplication.run(PayApplication.class, args);
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String greeting() {
		return "redirect:swagger-ui.html";
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
	}

	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select().apis(RequestHandlerSelectors.basePackage("com.pay.controller")).paths(PathSelectors.any()).build();
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				//页面标题
				.title("兴业银行 接口 API")
				//创建人
				.contact(new Contact("博彦开发部", "http://localhost:8765", "technicalSection@boyan.com"))
				//版本号
				.version("1.0")
				//描述
				.description("API 描述").build();
	}
}
