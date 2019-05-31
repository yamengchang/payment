package com.retailstore.config;

import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

@Controller
public class ErrorController {

	/**
	 * 404页面
	 */
	@GetMapping(value = "/error/404")
	public String error_404() {
		return "/error/error_404";
	}


	/**
	 * 500页面
	 */
	@GetMapping(value = "/500")
	public String error_500() {
		return "error_500";
	}

	/**
	 * 500页面
	 */
	@GetMapping(value = "/401")
	public String error_401() {
		return "error_401";
	}

}