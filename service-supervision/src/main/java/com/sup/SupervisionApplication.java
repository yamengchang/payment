package com.sup;

import com.sup.listener.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.*;
import org.springframework.cloud.client.discovery.*;
import org.springframework.cloud.openfeign.*;
import org.springframework.context.annotation.*;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import springfox.documentation.spi.*;
import springfox.documentation.spring.web.plugins.*;
import springfox.documentation.swagger2.annotations.*;

/**
 * Created by zwj on 2018/12/17.
 */
@EnableFeignClients
@EnableDiscoveryClient
@EnableSwagger2
@ComponentScan(value = {"com"})
@Import({AccountingDataDealWithListener.class, HouseDataDealWithListener.class, LeaseDataDealWithListener.class})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SupervisionApplication {
	public static void main(String[] args) {
		SpringApplication.run(SupervisionApplication.class, args);
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String greeting() {
		return "redirect:swagger-ui.html";
	}

	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select().apis(RequestHandlerSelectors.basePackage("com.sup.controller")).paths(PathSelectors.any()).build();
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("数据同步-MONGODB 接口 API").contact(new Contact("博彦开发部", "http://test.service.com:8001", "technicalSection@boyan.com")).version("1.0").description("API 描述").build();
	}
}
