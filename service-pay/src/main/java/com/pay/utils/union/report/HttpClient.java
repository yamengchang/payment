package com.pay.utils.union.report;

import javax.net.ssl.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpClient {

	private static class DefaultTrustManager implements X509TrustManager {
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}
	}

	public static String conHttp(String urlStr, Map<String, String> textParam, Map<String, Object> fileParams, String contentType) throws Exception {

		String boundary = System.currentTimeMillis() + ""; // 随机分隔线
		String ctype = "multipart/form-data;boundary=" + boundary + ";charset=UTF-8";
		URL url = new URL(urlStr);

		SSLSocketFactory socketFactory = null;
		HostnameVerifier verifier = null;
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
			ctx.getClientSessionContext().setSessionTimeout(15);
			ctx.getClientSessionContext().setSessionCacheSize(1000);
			socketFactory = ctx.getSocketFactory();
		} catch (Exception e) {

		}

		verifier = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
//				return false;// 默认认证不通过，进行证书校验。
				return true;// 默认认证不通过，进行证书校验。
			}
		};
		
		HttpsURLConnection httpurlconn = (HttpsURLConnection) url.openConnection();
		httpurlconn.setSSLSocketFactory(socketFactory);
		httpurlconn.setHostnameVerifier(verifier);

		httpurlconn.setDoOutput(true);
		httpurlconn.setDoInput(true);
		httpurlconn.setUseCaches(false);
		httpurlconn.setRequestProperty("Content-type", contentType==null?ctype:contentType);
		httpurlconn.setRequestMethod("POST");
		httpurlconn.connect();
		OutputStream out = httpurlconn.getOutputStream();

		byte[] entryBoundaryBytes = ("\r\n--" + boundary + "\r\n").getBytes("UTF-8");

		// 组装文本请求参数
		Set<Entry<String, String>> textEntrySet = textParam.entrySet();
		for (Entry<String, String> textEntry : textEntrySet) {
			byte[] textBytes = getTextEntry(textEntry.getKey(), textEntry.getValue(), "UTF-8");
			out.write(entryBoundaryBytes);
			out.write(textBytes);
		}

		if(null != fileParams && fileParams.size() > 0){
			// 组装二进制请求参数
			Set<Entry<String, Object>> fileEntrySet = fileParams.entrySet();
			for (Entry<String, Object> fileEntry : fileEntrySet) {
				Object fileItem = fileEntry.getValue();
				if (null != fileItem) {
					byte[] fileBytes = getFileEntry(fileEntry.getKey(), fileEntry.getKey(), "image/jpeg", "UTF-8");
					out.write(entryBoundaryBytes);
					out.write(fileBytes);
					out.write((byte[]) fileItem);
				}
			}
		}

		// 添加请求结束标志
		byte[] endBoundaryBytes = ("\r\n--" + boundary + "--\r\n").getBytes("UTF-8");
		out.write(endBoundaryBytes);

		out.flush();
		out.close();

		InputStream is = httpurlconn.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int len = 0;
		while ((len = is.read(buf, 0, 1024)) != -1) {
			baos.write(buf, 0, len);
		}

		String repStr = new String(baos.toByteArray(), "UTF-8");
		return repStr;
	}

	private static byte[] getTextEntry(String fieldName, String fieldValue, String charset) throws IOException {
		StringBuilder entry = new StringBuilder();
		entry.append("Content-Disposition:form-data;name=\"");
		entry.append(fieldName);
		entry.append("\"\r\nContent-Type:text/plain\r\n\r\n");
		entry.append(fieldValue);
		return entry.toString().getBytes(charset);
	}

	private static byte[] getFileEntry(String fieldName, String fileName, String mimeType, String charset)
			throws IOException {
		StringBuilder entry = new StringBuilder();
		entry.append("Content-Disposition:form-data;name=\"");
		entry.append(fieldName);
		entry.append("\";filename=\"");
		entry.append(fileName);
		entry.append("\"\r\nContent-Type:");
		entry.append(mimeType);
		entry.append("\r\n\r\n");
		return entry.toString().getBytes(charset);
	}

}
