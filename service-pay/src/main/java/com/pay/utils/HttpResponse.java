package com.pay.utils;

/**
 * HTTP响应封装类
 * @author wanglidong
 * @see HttpUtil
 */
public class HttpResponse {
	
	public HttpResponse() {
	}
	
	public HttpResponse(Integer code) {
		this.code = code;
	}
	
	public HttpResponse(String responseText) {
		this.responseText = responseText;
	}
	
	public HttpResponse(Integer code, String responseText) {
		this.code = code;
		this.responseText = responseText;
	}

	private int code;
	private String responseText;

	public Integer getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getResponseText() {
		return responseText;
	}

	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}
}