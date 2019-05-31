package com.pay.utils.https;

import com.omv.common.util.basic.ValueUtil;
import com.pay.utils.union.report.SSLClient;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static javax.xml.transform.OutputKeys.ENCODING;
import static org.springframework.web.servlet.support.WebContentGenerator.METHOD_GET;
import static org.springframework.web.servlet.support.WebContentGenerator.METHOD_POST;

/**
 * Created by MING on 2018/11/9.
 * Description:
 */
public class HttpsService {

    /*
     * 处理https GET/POST请求
     * 请求地址、请求方法、参数
     * */
    public static String httpsRequest(String requestUrl, String method, Map<String, String> header, String params) {
        StringBuffer buffer = null;
        try {
            //创建SSLContext
            SSLContext sslContext = SSLContext.getInstance("SSL");
            TrustManager[] tm = {new MyX509TrustManager()};
            HostnameVerifier verifier = null;
            //初始化
            sslContext.init(null, tm, new java.security.SecureRandom());
            //获取SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            verifier = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;// 默认认证不通过，进行证书校验。
                }
            };
            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            if (header != null) {
                for (String key : header.keySet()) {
                    conn.setRequestProperty(key,
                            header.get(key));
                }
            }
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(method);
            //设置当前实例使用的SSLSocketFactory
            conn.setSSLSocketFactory(ssf);
            conn.connect();
            //往服务器端写内容
            if (null != params) {
                OutputStream os = conn.getOutputStream();
                os.write(params.getBytes(StandardCharsets.UTF_8));
                os.close();
            }

            //读取服务器端返回的内容
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            buffer = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();

    }

    /**
     * 将参数化为 body
     *
     * @param params
     * @return
     */
    public static String getRequestBody(Map<String, String> params, boolean urlEncode) {
        StringBuilder body = new StringBuilder();
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (urlEncode) {
                try {
                    body.append(key).append("=").append(URLEncoder.encode(value, ENCODING)).append("&");
                } catch (UnsupportedEncodingException e) {
                    // e.printStackTrace();
                }
            } else {
                body.append(key).append("=").append(value).append("&");
            }
        }
        if (body.length() == 0) {
            return "";
        }
        return body.substring(0, body.length() - 1);
    }


    /**
     * 发送Https 并不校验证书
     *
     * @param url    地址
     * @param method post/get
     * @param header 请求头
     * @param map    数据 Map<String, Object>
     * @return str
     * @throws Exception ex
     */
    public static String sendRequestNoCheckCerPostMap(String url, String method, Map<String, String> header, Map<String, String> map) throws Exception {
        int timeout = 10;
        try (CloseableHttpClient httpclient = new SSLClient()) {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout * 1000)
                    .setConnectTimeout(timeout * 1000).setConnectionRequestTimeout(timeout * 1000).build();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            if (map != null) {
                Set<Map.Entry<String, String>> entrySet = map.entrySet();
                for (Map.Entry<String, String> e : entrySet) {
                    String name = e.getKey();
                    String value = String.valueOf(e.getValue());
                    NameValuePair pair = new BasicNameValuePair(name, value);
                    params.add(pair);
                }
            }
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, "UTF-8");
            HttpUriRequest reqMethod = null;
            if (METHOD_POST.equalsIgnoreCase(method)) {
                if (header != null) {
                    reqMethod = RequestBuilder.post().setUri(url)
//                            .setCharset(java.nio.charset.Charset.forName("UTF-8"))
//                            .addParameters(params.toArray(new BasicNameValuePair[params.size()]))
                            .setHeader("auth", header.get("auth"))
                            .setEntity(urlEncodedFormEntity).setConfig(requestConfig).build();
                } else {
                    reqMethod = RequestBuilder.post().setUri(url)
//                            .setCharset(java.nio.charset.Charset.forName("UTF-8"))
//                            .addParameters(params.toArray(new BasicNameValuePair[params.size()]))
//                            .setHeader("auto", header.get("auto"))
                            .setEntity(urlEncodedFormEntity).setConfig(requestConfig).build();
                }
            } else if (METHOD_GET.equalsIgnoreCase(method)) {
                reqMethod = RequestBuilder.get().setUri(url)
                        .setEntity(urlEncodedFormEntity)
                        .setHeader("auth", header.get("auth"))
//                        .addParameters(params.toArray(new BasicNameValuePair[params.size()]))
                        .setConfig(requestConfig).build();
            } else {
                return null;
            }
            CloseableHttpResponse response = null;
            response = httpclient.execute(reqMethod);
            String string = EntityUtils.toString(response.getEntity(), "UTF-8");
            System.out.println("statusCode: " + response.getStatusLine().getStatusCode());
            System.out.println("resp: " + string);
            if (response.getStatusLine().getStatusCode() == 200)
                return string;
            else {
                return string;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 发送Https 并不校验证书
     *
     * @param url    地址
     * @param method post/get
     * @param header 请求头
     * @param map    数据 Map<String, Object>
     * @throws Exception ex
     */
    public static void sendRequestNoCheckCerPostMapInputStream(String url, String method, Map<String, String> header, Map<String, String> map, HttpServletResponse response2, String templateNo) throws Exception {
        int timeout = 10;
        try (CloseableHttpClient httpclient = new SSLClient()) {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout * 1000)
                    .setConnectTimeout(timeout * 1000).setConnectionRequestTimeout(timeout * 1000).build();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            if (map != null) {
                Set<Map.Entry<String, String>> entrySet = map.entrySet();
                for (Map.Entry<String, String> e : entrySet) {
                    String name = e.getKey();
                    String value = String.valueOf(e.getValue());
                    NameValuePair pair = new BasicNameValuePair(name, value);
                    params.add(pair);
                }
            }
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, "UTF-8");
            HttpUriRequest reqMethod = null;
            if (METHOD_POST.equalsIgnoreCase(method)) {
                if (header != null) {
                    reqMethod = RequestBuilder.post().setUri(url)
//                            .setCharset(java.nio.charset.Charset.forName("UTF-8"))
//                            .addParameters(params.toArray(new BasicNameValuePair[params.size()]))
                            .setHeader("auth", header.get("auth"))
                            .setEntity(urlEncodedFormEntity).setConfig(requestConfig).build();
                } else {
                    reqMethod = RequestBuilder.post().setUri(url)
//                            .setCharset(java.nio.charset.Charset.forName("UTF-8"))
//                            .addParameters(params.toArray(new BasicNameValuePair[params.size()]))
//                            .setHeader("auto", header.get("auto"))
                            .setEntity(urlEncodedFormEntity).setConfig(requestConfig).build();
                }
            } else if (METHOD_GET.equalsIgnoreCase(method)) {
                reqMethod = RequestBuilder.get().setUri(url)
                        .setEntity(urlEncodedFormEntity)
                        .setHeader("auth", header.get("auth"))
//                        .addParameters(params.toArray(new BasicNameValuePair[params.size()]))
                        .setConfig(requestConfig).build();
            } else {
                return;
            }
            CloseableHttpResponse response = null;
            response = httpclient.execute(reqMethod);
            System.out.println("statusCode: " + response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == 200) {
                OutputStream os = new BufferedOutputStream(response2.getOutputStream());
                // 设置输出的格式
                response2.reset();
                response2.setContentType("application/x-msdownload");
                response2.addHeader("Content-Disposition", "attachment; filename=\"" +
                        new String((templateNo + ".pdf").getBytes(StandardCharsets.UTF_8),
                                "iso8859-1") + "\";charset=UTF-8");
                response2.setCharacterEncoding("UTF-8");
                //一次读出10兆
                byte[] b = new byte[1024 * 1024 * 10];
                int len;
                while ((len = response.getEntity().getContent().read(b)) > 0) {
                    os.write(b, 0, len);
                }
                os.flush();
                os.close();
            } else {
                String msg = ValueUtil.getFromJson(EntityUtils.toString(response.getEntity(), "UTF-8"), "msg");
                ValueUtil.isError(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
