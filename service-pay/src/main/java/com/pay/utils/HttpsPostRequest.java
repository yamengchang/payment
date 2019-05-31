///**
// * 使用URLConnection的通讯器
// */
//package com.pay.utils;
//
//import com.cib.fintech.dfp.open.sdk.config.*;
//import com.cib.fintech.dfp.open.sdk.enums.*;
//import com.cib.fintech.dfp.open.sdk.util.Const;
//
//import javax.net.ssl.*;
//import java.io.*;
//import java.net.*;
//import java.security.*;
//import java.util.*;
//
//public class HttpsPostRequest extends ITransService {
//
//    private static final int BUFFER_SIZE = 2048;
//
//    @Override
//    public Object send(String url, ReqMethodEnum method, String authInfo, Map<String, String> headParams,
//            String bodyParams) throws Exception {
//        URL connUrl = null;
//        HttpURLConnection conn = null;
//        Object retn = null;
//        OutputStream outStream = null;
//        InputStream inStream = null;
//        System.out.println(authInfo);
//        try {
//            // 打开URL连接
//            connUrl = new URL(url);
//            conn = (HttpURLConnection) connUrl.openConnection();
//
//            // 测试环境忽略SSL证书验证
//            if (Configure.isDevEnv() && connUrl.getProtocol().equals("https")) {
//                ignoreSSLVerify((HttpsURLConnection) conn);
//            }
//
//            // 设置HEAD信息
//            conn.setRequestProperty(Const.POST_HEAD_KEY, authInfo);
//            if (headParams != null) {
//                for (String key : headParams.keySet()) {
//                    conn.setRequestProperty(key, headParams.get(key));
//                }
//            }
//
//            // POST方式处理
//            switch (method) {
//            case POST:
//                conn.setRequestMethod(ReqMethodEnum.POST.toString());
//                conn.setDoOutput(true);
//                outStream = conn.getOutputStream();
//                System.out.println(bodyParams);
//                outStream.write(bodyParams.getBytes(Const.CHARSET));
//                outStream.flush();
//                break;
//            default:
//                break;
//            }
//
//            // 获取返回结果
//            int code = conn.getResponseCode();
//            if (code >= 200 && code < 300 && code != 0) {
//                inStream = conn.getInputStream();
//            } else {
//                inStream = conn.getErrorStream();
//            }
//            byte[] bin = readInputStream(inStream);
//            if ("application/octet-stream".equals(conn.getContentType())) {
//                retn = bin;
//            } else {
//                retn = new String(bin, Const.CHARSET);
//            }
//        } finally {
//            if (inStream != null) {
//                inStream.close();
//            }
//            if (outStream != null) {
//                outStream.close();
//            }
//            if (conn != null) {
//                conn.disconnect();
//            }
//        }
//        return retn;
//    }
//
//    private static byte[] readInputStream(InputStream inStream) throws IOException {
//        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//        byte[] data = null;
//        byte[] buffer = new byte[BUFFER_SIZE];
//        int len = 0;
//        try {
//            while ((len = inStream.read(buffer)) != -1) {
//                outStream.write(buffer, 0, len);
//            }
//            data = outStream.toByteArray();
//        } finally {
//            if (outStream != null) {
//                outStream.close();
//            }
//        }
//        return data;
//    }
//
//    private static void ignoreSSLVerify(HttpsURLConnection conn)
//            throws NoSuchAlgorithmException, KeyManagementException {
//        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
//            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                return null;
//            }
//
//            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
//            }
//
//            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
//            }
//        } };
//
//        SSLContext sc = SSLContext.getInstance("SSL");
//        sc.init(null, trustAllCerts, new java.security.SecureRandom());
//
//        conn.setSSLSocketFactory(sc.getSocketFactory());
//
//        conn.setHostnameVerifier(new HostnameVerifier() {
//
//            public boolean verify(String hostname, SSLSession session) {
//                return true;
//            }
//        });
//    }
//
//}