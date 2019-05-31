//package com.retailstore.ribbon;
//
////import com.omv.common.util.basic.HttpServletUtil;
////import com.omv.common.util.basic.ValueUtil;
//import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.client.ClientHttpResponse;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.Charset;
//
///**
// * Created by zwj on 2018/7/17.
// */
//
////@Component
//public class ZuulFallRibbon implements FallbackProvider {
//    @Override
//    public String getRoute() {
//        HttpServletRequest request = HttpServletUtil.getRequests();
//        if (request == null) {
//            return null;
//        }
//        String url = request.getRequestURI();
//        url = url.substring(1);
//        String route = url.substring(0, url.indexOf("/"));
//        return route;
//    }
//
//    @Override
//    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
//        return new ClientHttpResponse() {
//            @Override
//            public HttpStatus getStatusCode() throws IOException {
//                return HttpStatus.OK;
//            }
//
//            @Override
//            public int getRawStatusCode() throws IOException {
//                return this.getStatusCode().value();
//            }
//
//            @Override
//            public String getStatusText() throws IOException {
//                return this.getStatusCode().getReasonPhrase();
//            }
//
//            @Override
//            public void close() {
//
//            }
//
//            @Override
//            public InputStream getBody() throws IOException {
//                return new ByteArrayInputStream(ValueUtil.toError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Connect to server : "+ getRoute() +" failed").getBytes());
//            }
//
//            @Override
//            public HttpHeaders getHeaders() {
//                HttpHeaders headers = new HttpHeaders();
//                MediaType mt = new MediaType("application", "json", Charset.forName("UTF-8"));
//                headers.setContentType(mt);
//                return headers;
//            }
//
//        };
//    }
//}
