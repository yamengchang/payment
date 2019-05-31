package com.pay.utils.union.report;

import com.omv.common.util.basic.ValueUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SignTermUtil {


    public static String signData(List<BasicNameValuePair> nvps,String acqPreKey) throws Exception {

        TreeMap<String, String> tempMap = new TreeMap<String, String>();
        for (BasicNameValuePair pair : nvps) {
            if (StringUtils.isNotBlank(pair.getValue())) {
                tempMap.put(pair.getName(), pair.getValue());
            }
        }
        StringBuffer buf = new StringBuffer();
        for (String key : tempMap.keySet()) {
            buf.append(key).append("=").append((String) tempMap.get(key)).append("&");
        }
        String signatureStr = buf.substring(0, buf.length() - 1);
        String signData = RSAUtil.signByPrivate(signatureStr, acqPreKey, "UTF-8");
        System.out.println("请求数据：" + signatureStr + "&signature=" + signData);
        return signData;
    }

    public static Map<String,String> verferSignData(String str,String acqPubKey) throws UnsupportedEncodingException {
        System.out.println("响应数据：" + str);
        String data[] = str.split("&");
        StringBuffer buf = new StringBuffer();
        String signature = "";
        Map<String,String> responseMap = new HashMap<>();
        for (int i = 0; i < data.length; i++) {
            String tmp[] = data[i].split("=", 2);
            responseMap.put(tmp[0],tmp[1]);
            if ("signature".equals(tmp[0])) {
                signature = tmp[1];
            } else {
                buf.append(tmp[0]).append("=").append(tmp[1]).append("&");
            }
        }
        String signatureStr = buf.substring(0, buf.length() - 1);
//        String respCode = responseMap.get("respCode");
//        if(!respCode.equals("0000")){
//            String errorMsg = responseMap.get("respDesc");
//            ValueUtil.isError(errorMsg);
//        }
        if(!RSAUtil.verifyByKeyPath(signatureStr, signature,acqPubKey, "UTF-8")){
            if(responseMap.get("respDesc")!=null){
                ValueUtil.isError(responseMap.get("respDesc"));
            }
            ValueUtil.isError("验签失败");
        }
        return responseMap;
    }
}
