//package com.retailstore.security;
//
//import com.retailstore.config.*;
//import org.apache.commons.lang3.*;
//import org.springframework.security.authentication.*;
//import org.springframework.security.core.context.*;
//import org.springframework.security.web.authentication.www.*;
//
//import javax.servlet.*;
//import javax.servlet.http.*;
//import java.io.*;
//import java.util.*;
//
//public class JWTAuthenticationFilter extends BasicAuthenticationFilter {
//
//	public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
//		super(authenticationManager);
//	}
//
//
//	/**
//	 * 在此方法中检验客户端请求头中的token,
//	 * 如果存在并合法,就把token中的信息封装到 Authentication 类型的对象中,
//	 * 最后使用  SecurityContextHolder.getContext().setAuthentication(authentication); 改变或删除当前已经验证的 pricipal
//	 * @param request
//	 * @param response
//	 * @param chain
//	 * @throws java.io.IOException
//	 * @throws javax.servlet.ServletException
//	 */
//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
//		org.springframework.security.core.context.SecurityContext context = SecurityContextHolder.getContext();
//		if (context != null && context.getAuthentication() != null && context.getAuthentication().getCredentials() != null) {
//			chain.doFilter(request, response);
//			return;
//		}else {
//			String token = request.getParameter("access_token");
//			if (StringUtils.isNotBlank(token)) {
//				Map map = Utils.decode(token);
//				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(map.get("user_name"), null, new ArrayList<>());
//				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//			}else {
//				SecurityContextHolder.clearContext();
//			}
//		}
//		chain.doFilter(request, response);
//		return;
//	}
//
//}