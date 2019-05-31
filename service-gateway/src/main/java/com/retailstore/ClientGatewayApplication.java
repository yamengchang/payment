package com.retailstore;

import com.netflix.hystrix.contrib.metrics.eventstream.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.web.server.*;
import org.springframework.boot.web.servlet.*;
import org.springframework.cloud.client.circuitbreaker.*;
import org.springframework.cloud.client.discovery.*;
import org.springframework.cloud.netflix.hystrix.*;
import org.springframework.cloud.netflix.hystrix.dashboard.*;
import org.springframework.cloud.netflix.zuul.*;
import org.springframework.cloud.openfeign.*;
import org.springframework.context.annotation.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.servlet.config.annotation.*;

@SpringBootApplication
@EnableZuulProxy
@EnableHystrix
@EnableDiscoveryClient
@EnableHystrixDashboard
@EnableCircuitBreaker
@EnableFeignClients
public class ClientGatewayApplication {
	public static void main(String[] args) {
		SpringApplication.run(ClientGatewayApplication.class, args);
	}

	@Configuration
	public class WebMvcConfig extends WebMvcConfigurerAdapter {
		@Override
		public void addCorsMappings(CorsRegistry registry) {
			registry.addMapping("/**").allowedOrigins("*").allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE").maxAge(3600).allowCredentials(false);
		}
	}

	@Bean
	public ServletRegistrationBean getServlet() {
		HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
		registrationBean.setLoadOnStartup(1);
		registrationBean.addUrlMappings("/turbine/hystrix.stream");
		registrationBean.setName("HystrixMetricsStreamServlet");
		return registrationBean;
	}

	@Component
	public class ErrorConfiguration implements ErrorPageRegistrar {
		@Override
		public void registerErrorPages(ErrorPageRegistry registry) {
			ErrorPage[] errorPages = new ErrorPage[] {new ErrorPage(HttpStatus.NOT_FOUND, "/error/404"), new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500"), new ErrorPage(HttpStatus.UNAUTHORIZED, "/error/401"), new ErrorPage(Throwable.class, "/error/500")};
			registry.addErrorPages(errorPages);
		}
	}
}
