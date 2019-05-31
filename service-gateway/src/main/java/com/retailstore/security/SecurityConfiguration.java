package com.retailstore.security;

import com.netflix.zuul.context.*;
import com.retailstore.config.*;
import com.retailstore.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.autoconfigure.security.oauth2.client.*;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.core.context.*;
import org.springframework.security.oauth2.client.filter.*;
import org.springframework.security.web.authentication.www.*;
import org.springframework.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

/**
 * @author Thibaud
 */
@Configuration
@EnableOAuth2Sso
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Value("${black.ip}")
	private String blackip;

	@Value("${black.cookie}")
	private String blackcookie;
	@Autowired
	private FeignService feignService;

	@Bean
	@Primary
	public OAuth2ClientContextFilter dynamicOauth2ClientContextFilter() {
		return new DynamicOauth2ClientContextFilter();
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests().antMatchers("/turbine/**", "/webjars/**", "/proxy.stream", "/**/napi/**", "/error_404", "/uaa/**", "/login", "/hystrix/**", "/**/scrt/**", "/**/callback/**", "/**/css/**", "/**/img/**", "/**/js/**").permitAll().and().authorizeRequests().anyRequest().authenticated().and().headers().frameOptions().disable().and().logout().logoutUrl("/logout").logoutSuccessUrl("/uaa/logout").permitAll();
		http.addFilterBefore(new BasicAuthenticationFilter(authenticationManager()) {
			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
				Assert(checkDoamin(request), response, "无效的token");
				Assert(canIgnore(request), response, "您已被禁止访问");
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				if (authentication != null) {
					RequestContext requestContext = RequestContext.getCurrentContext();
					//			客户端通过 获取用户信息		((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getHeader("X-AUTH-ID");
					requestContext.addZuulRequestHeader("X-AUTH-ID", authentication.getPrincipal().toString());
				}
				if (check(request)) {
					response.sendRedirect("error_404");
					return;
				}
				chain.doFilter(request, response);
				return;
			}
		}, BasicAuthenticationFilter.class);
	}

	private boolean canIgnore(HttpServletRequest request) {
		String url = request.getRequestURI();
		if (url.contains("error_404")) {
			return false;
		}
		return true;
	}

	private boolean checkDoamin(HttpServletRequest request) throws IOException, ServletException {
		String serverName = request.getServerName(); //获取域名
		String access_token = request.getParameter("access_token"); //如果后面带有路径的,则不拦截
		if (StringUtils.isNotEmpty(access_token)) {
			Map map = feignService.check(access_token);
			Assert.notEmpty(map, "获取用户信息失败");
			//			Map clientDetails = (Map)map.get("map");
			try {
				Map oAuth2Authenticatio = (Map)map.get("oAuth2Authentication");
				SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(oAuth2Authenticatio.get("principal"), oAuth2Authenticatio.get("credentials"), buildUserAuthority(null)));

			}catch (Exception e) {
				return false;
			}

		}
		return true;
	}

	private List<GrantedAuthority> buildUserAuthority(List userRoles) {
		Set<GrantedAuthority> setAuths = new HashSet<>();
		if (userRoles == null) {
			setAuths.add(new SimpleGrantedAuthority("admin"));
			return new ArrayList<>(setAuths);
		}
		return new ArrayList<>(setAuths);
	}

	public boolean check(HttpServletRequest request) {
		boolean ip = Arrays.asList(blackip.split(",")).contains(Utils.getIpAddress(request));

		Cookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length == 0) {
			return ip;
		}
		List<String> cookieList = new ArrayList<String>(4);
		for (Cookie cookie : cookies) {
			cookieList.add(cookie.getValue());
		}
		cookieList.retainAll(Arrays.asList(blackcookie.split(",")));
		return cookieList.isEmpty() && ip;
	}

	private void Assert(boolean expression, HttpServletResponse response, String msg) {
		if (!expression) {
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Content-type", "text/html;charset=UTF-8");
			PrintWriter out = null;
			try {
				out = response.getWriter();
				out.append(msg);
			}catch (IOException e) {
				e.printStackTrace();
			}finally {
				if (out != null) {
					out.close();
				}
			}
			Assert.isTrue(false, msg);
		}
	}
}
