package com.retailstore.security;

import org.springframework.security.oauth2.client.filter.*;
import org.springframework.security.oauth2.client.resource.*;
import org.springframework.security.web.*;
import org.springframework.web.util.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;

public class DynamicOauth2ClientContextFilter extends OAuth2ClientContextFilter {
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	protected void redirectUser(UserRedirectRequiredException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String redirectUri = e.getRedirectUri();
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(redirectUri);
		Map<String, String> requestParams = e.getRequestParams();
		for (Map.Entry<String, String> param : requestParams.entrySet()) {
				builder.queryParam(param.getKey(), param.getValue());
		}
		if (e.getStateKey() != null) {
			builder.queryParam("state", e.getStateKey());
		}
		String url = getBaseUrl(request) + builder.build().encode().toUriString();
		this.redirectStrategy.sendRedirect(request, response, url);
	}

	@Override
	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}

	private String getBaseUrl(HttpServletRequest request) {
		StringBuffer url = request.getRequestURL();
		return url.substring(0, url.length() - request.getRequestURI().length() + request.getContextPath().length());
	}
}
