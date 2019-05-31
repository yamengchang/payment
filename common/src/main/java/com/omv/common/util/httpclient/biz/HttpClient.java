package com.omv.common.util.httpclient.biz;

import com.omv.common.util.basic.ValueUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
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

    public static String httpPostFile(String url, Map<String,String> textParams,Map<String, MultipartFile> filePrams) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse httpResponse;
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(200000).setSocketTimeout(200000000).build();
        HttpPost httpPost = new HttpPost(url);
        String boundary = System.currentTimeMillis() + "";
        httpPost.setConfig(requestConfig);
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

        ContentType strContent=ContentType.create("text/plain", Charset.forName("UTF-8"));

        if(null!=filePrams&&filePrams.size()>0){
            Iterator iterator = filePrams.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String) entry.getKey();
                MultipartFile file = (MultipartFile) entry.getValue();
                String fileNam = file.getName();
                multipartEntityBuilder.addBinaryBody(key, file.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileNam);

            }
        }

        if(null!=textParams&&textParams.size()>0){
            Iterator iterator = textParams.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String) entry.getKey();
                String val = (String) entry.getValue();
                multipartEntityBuilder.addTextBody(key, val,strContent);
            }
        }
        HttpEntity httpEntity = multipartEntityBuilder.build();
        httpPost.setEntity(httpEntity);

        httpResponse = httpClient.execute(httpPost);
        HttpEntity responseEntity = httpResponse.getEntity();
        int statusCode= httpResponse.getStatusLine().getStatusCode();
        String result = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
        System.out.println(result);
        httpClient.close();
        httpResponse.close();
        return result;
    }

    public static String conHttpByJson(String url, Object obj,Map<String,String> header) {
        try {
            final String CONTENT_TYPE_TEXT_JSON = "text/json";
            DefaultHttpClient client = new DefaultHttpClient(
                    new PoolingClientConnectionManager());

            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
            if(null!=header&&header.size()>0){
                Iterator iterator = header.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry entry = (Map.Entry) iterator.next();
                    String key = (String) entry.getKey();
                    String val = (String) entry.getValue();
                    httpPost.setHeader(key, val);

                }
            }

            StringEntity se = new StringEntity(ValueUtil.toString(obj));
            se.setContentType(CONTENT_TYPE_TEXT_JSON);
            httpPost.setEntity(se);

            CloseableHttpResponse response = client.execute(httpPost);
            HttpEntity entity = null;
            entity = response.getEntity();
            String s2 = EntityUtils.toString(entity, "UTF-8");
            return s2;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String conHttpsByForm(String urlStr, Map<String, String> textParam, Map<String, Object> fileParams, String contentType) throws Exception {

        String boundary = System.currentTimeMillis() + ""; // 随机分隔线
        String ctype = "multipart/form-data;boundary=" + boundary + ";charset=UTF-8";
        URL url = new URL(urlStr);

        SSLSocketFactory socketFactory = null;
        HostnameVerifier verifier = null;
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
            ctx.getClientSessionContext().setSessionTimeout(150);
            ctx.getClientSessionContext().setSessionCacheSize(10000);
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
        httpurlconn.setRequestProperty("Content-type", contentType == null ? ctype : contentType);
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

        if (null != fileParams && fileParams.size() > 0) {
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

    public static void main(String[] args) {
        Map<String,String> header = new HashMap<>();
        Map<String,String> body = new HashMap<>();
        header.put("custom","123");
        body.put("storeName","t");
        conHttpByJson("http://localhost:8102/store/manage",body,header);
    }
}
