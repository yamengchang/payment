package com.retailstore.config;

import org.springframework.core.io.*;
import org.springframework.security.jwt.*;
import org.springframework.security.jwt.crypto.sign.*;
import org.springframework.security.oauth2.common.exceptions.*;
import org.springframework.security.oauth2.common.util.*;
import org.springframework.security.oauth2.provider.token.store.*;

import javax.servlet.http.*;
import java.io.*;
import java.net.*;
import java.security.interfaces.*;
import java.util.*;

import static org.springframework.security.oauth2.provider.token.AccessTokenConverter.*;

public class Utils {
	public static String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}


	private static JsonParser objectMapper = JsonParserFactory.create();
	private static SignatureVerifier verifier = new RsaVerifier((RSAPublicKey)new KeyStoreKeyFactory(new ClassPathResource("mytest.jks"), "mypass".toCharArray()).getKeyPair("mytest").getPublic());

	public static Map<String, Object> decode(String token) {
		try {
			Jwt jwt = JwtHelper.decodeAndVerify(token, verifier);
			String content = jwt.getClaims();
			Map<String, Object> map = objectMapper.parseMap(content);
			if (map.containsKey(EXP) && map.get(EXP) instanceof Integer) {
				Integer intValue = (Integer)map.get(EXP);
				map.put(EXP, new Long(intValue));
			}
			return map;
		}catch (Exception e) {
			throw new InvalidTokenException("Cannot convert access token to JSON", e);
		}
	}

	/**
	 * 根据cookieName返回该cookie
	 * @param request
	 * @param cookieName
	 * @return
	 * @throws java.io.UnsupportedEncodingException
	 */
	public static Cookie getCookieByName(HttpServletRequest request, String cookieName) throws UnsupportedEncodingException {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(cookieName)) {
				return new Cookie(cookie.getName(), URLDecoder.decode(cookie.getValue(), "UTF-8"));
			}
		}
		return null;
	}

}
