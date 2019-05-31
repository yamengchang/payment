package com.pay.utils;

import jodd.util.*;
import org.apache.http.Header;
import org.apache.http.*;
import org.apache.http.client.config.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.*;
import org.apache.http.conn.*;
import org.apache.http.entity.*;
import org.apache.http.entity.mime.*;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.*;
import org.apache.http.message.*;
import org.apache.http.protocol.*;
import org.apache.http.util.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * HTTP工具类
 * @author wanglidong
 */
public class HttpUtil {

	public static final String DEFAULT_CHARSET = "UTF-8";

	private static final RequestConfig CONFIG = RequestConfig.custom().setConnectTimeout(10000).setSocketTimeout(10000).build();

	private static final PoolingHttpClientConnectionManager POOLING_HTTP_CLIENT_CONNECTION_MANAGER = new PoolingHttpClientConnectionManager();

	static {
		POOLING_HTTP_CLIENT_CONNECTION_MANAGER.setMaxTotal(200);
		POOLING_HTTP_CLIENT_CONNECTION_MANAGER.setDefaultMaxPerRoute(20);
		new Timer(true).schedule(new IdleConnectionMonitor(POOLING_HTTP_CLIENT_CONNECTION_MANAGER), 60000, 60000);
	}

	private static final ConnectionKeepAliveStrategy KEEP_ALIVE_STRATEGY = new ConnectionKeepAliveStrategy() {
		@Override
		public long getKeepAliveDuration(org.apache.http.HttpResponse response, HttpContext context) {
			HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
			while (it.hasNext()) {
				HeaderElement he = it.nextElement();
				String param = he.getName();
				String value = he.getValue();
				if (value != null && param.equalsIgnoreCase("timeout")) {
					try {
						return Long.valueOf(value) * 1000;
					}catch (NumberFormatException e) {
						e.printStackTrace();
						break;
					}
				}
			}
			return 30 * 1000;
		}
	};

	private static CloseableHttpClient getHttpClient() {
		return HttpClients.custom().setKeepAliveStrategy(KEEP_ALIVE_STRATEGY).setConnectionManager(POOLING_HTTP_CLIENT_CONNECTION_MANAGER).setConnectionManagerShared(true).build();
	}

	public static HttpResponse get(String url, Header... headers) throws IOException {
		return get(url, (Map<String, Object>)null, headers);
	}

	public static HttpResponse get(String url, NameValuePair[] nameValuePair, Header... headers) throws IOException {
		Map<String, Object> paramsMap = new HashMap<>(nameValuePair.length);
		for (NameValuePair t : nameValuePair) {
			paramsMap.put(t.getName(), t.getValue());
		}

		return get(url, paramsMap, headers);
	}

	public static HttpResponse get(String url, Map<String, Object> paramsMap, Header... headers) throws IOException {
		StringBuilder builder = new StringBuilder();
		if (paramsMap != null) {
			for (Map.Entry<String, Object> param : paramsMap.entrySet()) {
				builder.append("&" + param.getKey() + "=" + param.getValue());
			}
			url = url + "?" + builder.toString().substring(1);
		}

		HttpGet method = new HttpGet(url);
		method.setConfig(CONFIG);
		if (null != headers && headers.length > 0) {
			method.setHeaders(headers);
		}

		CloseableHttpResponse response = getHttpClient().execute(method);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			return new HttpResponse(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity, DEFAULT_CHARSET));
		}

		return new HttpResponse(response.getStatusLine().getStatusCode());

	}

	public static int head(String url) {
		HttpHead method = new HttpHead(url);
		method.setConfig(CONFIG);

		try (CloseableHttpResponse response = getHttpClient().execute(method)) {
			return response.getStatusLine().getStatusCode();
		}catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static HttpResponse post(String url, NameValuePair[] nameValuePair, Header... headers) throws UnsupportedEncodingException {
		Map<String, String> paramsMap = new HashMap<String, String>(nameValuePair.length);
		for (NameValuePair t : nameValuePair) {
			paramsMap.put(t.getName(), t.getValue());
		}
		return post(url, paramsMap, headers);
	}

	public static HttpResponse post(String url, Map<String, String> paramsMap, Header... headers) throws UnsupportedEncodingException {
		HttpPost method = new HttpPost(url);
		method.setConfig(CONFIG);
		if (paramsMap != null) {
			List<org.apache.http.NameValuePair> paramList = new ArrayList<org.apache.http.NameValuePair>();
			for (Map.Entry<String, String> param : paramsMap.entrySet()) {
				paramList.add(new BasicNameValuePair(param.getKey(), param.getValue()));
			}
			method.setEntity(new UrlEncodedFormEntity(paramList, DEFAULT_CHARSET));

		}
		if (null != headers && headers.length > 0) {
			method.setHeaders(headers);
		}

		try (CloseableHttpResponse response = getHttpClient().execute(method)) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return new HttpResponse(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity, DEFAULT_CHARSET));
			}

			return new HttpResponse(response.getStatusLine().getStatusCode());
		}catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static HttpResponse post(String url, String body, String contentType, Header... headers) {
		StringEntity requestBody = new StringEntity(body, DEFAULT_CHARSET);
		requestBody.setContentType(StringUtil.isNotBlank(contentType) ?contentType : "application/octet-stream");

		HttpPost method = new HttpPost(url);
		method.setConfig(CONFIG);
		method.setEntity(requestBody);
		if (null != headers && headers.length > 0) {
			method.setHeaders(headers);
		}

		try (CloseableHttpResponse response = getHttpClient().execute(method)) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return new HttpResponse(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity, DEFAULT_CHARSET));
			}

			return new HttpResponse(response.getStatusLine().getStatusCode());
		}catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static HttpResponse postFormData(String url, Map<String, String> paramsMap, Header... headers) {
		HttpPost method = new HttpPost(url);
		method.setConfig(CONFIG);

		if (paramsMap != null) {
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			for (Map.Entry<String, String> param : paramsMap.entrySet()) {
				builder.addTextBody(param.getKey(), param.getValue());
			}
			method.setEntity(builder.build());
		}
		if (null != headers && headers.length > 0) {
			method.setHeaders(headers);
		}

		try (CloseableHttpResponse response = getHttpClient().execute(method)) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return new HttpResponse(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity, DEFAULT_CHARSET));
			}

			return new HttpResponse(response.getStatusLine().getStatusCode());
		}catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static HttpResponse put(String url, NameValuePair[] nameValuePair, Header... headers) {
		Map<String, String> paramsMap = new HashMap<String, String>(nameValuePair.length);
		for (NameValuePair t : nameValuePair) {
			paramsMap.put(t.getName(), t.getValue());
		}
		return put(url, paramsMap, headers);
	}

	public static HttpResponse put(String url, Map<String, String> paramsMap, Header... headers) {
		HttpPut method = new HttpPut(url);
		method.setConfig(CONFIG);

		if (paramsMap != null) {
			try {
				List<org.apache.http.NameValuePair> paramList = new ArrayList<org.apache.http.NameValuePair>();
				for (Map.Entry<String, String> param : paramsMap.entrySet()) {
					paramList.add(new BasicNameValuePair(param.getKey(), param.getValue()));
				}
				method.setEntity(new UrlEncodedFormEntity(paramList, DEFAULT_CHARSET));
			}catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
		if (null != headers && headers.length > 0) {
			method.setHeaders(headers);
		}

		try (CloseableHttpResponse response = getHttpClient().execute(method)) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return new HttpResponse(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity, DEFAULT_CHARSET));
			}

			return new HttpResponse(response.getStatusLine().getStatusCode());
		}catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static HttpResponse put(String url, String body, String contentType, Header... headers) {
		StringEntity requestBody = new StringEntity(body, DEFAULT_CHARSET);
		requestBody.setContentType(StringUtil.isNotBlank(contentType) ?contentType : "application/octet-stream");

		HttpPut method = new HttpPut(url);
		method.setConfig(CONFIG);
		method.setEntity(requestBody);
		if (null != headers && headers.length > 0) {
			method.setHeaders(headers);
		}

		try (CloseableHttpResponse response = getHttpClient().execute(method)) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return new HttpResponse(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity, DEFAULT_CHARSET));
			}

			return new HttpResponse(response.getStatusLine().getStatusCode());
		}catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static HttpResponse delete(String url, Header... headers) {
		HttpDelete method = new HttpDelete(url);
		method.setConfig(CONFIG);
		if (null != headers && headers.length > 0) {
			method.setHeaders(headers);
		}

		try (CloseableHttpResponse response = getHttpClient().execute(method)) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return new HttpResponse(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity, DEFAULT_CHARSET));
			}

			return new HttpResponse(response.getStatusLine().getStatusCode());
		}catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static class IdleConnectionMonitor extends TimerTask {

		private final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;

		private IdleConnectionMonitor(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager) {
			this.poolingHttpClientConnectionManager = poolingHttpClientConnectionManager;
		}

		@Override
		public void run() {
			// 关闭失效的连接
			poolingHttpClientConnectionManager.closeExpiredConnections();
			// 可选的, 关闭30秒内不活动的连接
			poolingHttpClientConnectionManager.closeIdleConnections(30, TimeUnit.SECONDS);
		}
	}
}