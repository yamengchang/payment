package com.sup.util;

import com.omv.common.util.basic.ValueUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.*;

/**
 * Created by zwj on 2019/1/8.
 */
public class HttpClient {
//    public static void main(String[] args) {
//        Map<String,String> map = new HashMap<>();
//        map.put("storeName","aaa");
//        List list = new ArrayList<>();
//        list.add(map);
//        try {
////            String result = com.omv.common.util.httpclient.biz.HttpClient.conHttp("http://localhost:8102/complete/user/info",map,null,"application/json");
//            System.out.println(conHttp("http://localhost:8102/complete/user/info",list));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
