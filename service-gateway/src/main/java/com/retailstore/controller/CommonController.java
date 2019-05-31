package com.retailstore.controller;

import com.netflix.hystrix.contrib.javanica.annotation.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.*;
import org.springframework.security.core.context.*;
import org.springframework.security.jwt.*;
import org.springframework.security.jwt.crypto.sign.*;
import org.springframework.security.oauth2.common.util.*;
import org.springframework.security.oauth2.provider.token.store.*;
import org.springframework.security.web.*;
import org.springframework.stereotype.*;
import org.springframework.util.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.security.interfaces.*;
import java.util.*;

import static org.springframework.security.oauth2.provider.token.AccessTokenConverter.*;

@Controller
public class CommonController {

	@RequestMapping("/user")
	@ResponseBody
	public Object user(HttpServletRequest request, HttpServletRequest response) {
		SecurityContext context = SecurityContextHolder.getContext();
		if (context == null || context.getAuthentication() == null) {
			String token = request.getParameter("access_token");
			if (StringUtils.isNotBlank(token)) {
				Map map = decode(token);
				return map;
			}
			Assert.notNull(null, "获取用户信息失败");
		}else {
			return context.getAuthentication();
		}
		return null;
	}

	@GetMapping(value = "/")
	@ResponseBody
	public Object index(HttpServletRequest request, HttpServletRequest response) {
		SecurityContext context = SecurityContextHolder.getContext();
		if (context == null || context.getAuthentication() == null) {
			String token = request.getParameter("access_token");
			if (StringUtils.isNotBlank(token)) {
				Map map = decode(token);
				return map;
			}
			Assert.notNull(null, "获取用户信息失败");
		}else {
			return context.getAuthentication();
		}
		return null;
	}


	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@RequestMapping("/napi/auth2/{state}")
	public void auth(@PathVariable("state") String state, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String baseurl = "http://test.service.com:8765/uaa/oauth/authorize?client_id=retailstore&redirect_uri=http://www.baidu.com&response_type=code&state=" + state;
		response.sendRedirect(baseurl);
		return;
	}

	@RequestMapping("/napi/{type}")
	@HystrixCommand(fallbackMethod = "getFallback")
	public String hy(@PathVariable("type") Integer type) throws Exception {

		if (type == 1) {    // 数据不存在，假设让它抛出个错误
			throw new RuntimeException("错误了！");
		}
		return "服务没有降级返回值";
	}

	public String getFallback(@PathVariable("id") Integer id) {    // 此时方法的参数 与get()一致
		return "服务降级了";
	}


	private static JsonParser objectMapper = JsonParserFactory.create();
	private static SignatureVerifier verifier = new RsaVerifier((RSAPublicKey)new KeyStoreKeyFactory(new ClassPathResource("mytest.jks"), "mypass".toCharArray()).getKeyPair("mytest").getPublic());

	public Map<String, Object> decode(String token) {
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
			Assert.isTrue(false, "无效的token");
			return null;
		}
	}

}
