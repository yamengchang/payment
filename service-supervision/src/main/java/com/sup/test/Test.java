package com.sup.test;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.omv.common.util.basic.MapUtil;
import com.omv.common.util.httpclient.biz.*;
import com.omv.common.util.signature.*;
import org.springframework.util.*;

import java.io.*;
import java.util.*;

/**
 * Created by zwj on 2018/8/11.
 */
public class Test {
    static String str = "{\n" +
            "\t\"instanceId\":\"I000000000000000001\",\n" +
            "\t\"notifyUrl\":\"www.baidu.com\",\n" +
            "\t\"timestamp\":\"1231239233\",\n" +
            "\t\"data\":[{\n" +
            "            \"address\": \"string\",\n" +
            "            \"airConditioningCost\": \"string\",\n" +
            "            \"alias\": \"string\",\n" +
            "            \"buildType\": \"string\",\n" +
            "            \"buildTypeName\": \"string\",\n" +
            "            \"buildingCode\": \"string\",\n" +
            "            \"cityName\": \"string\",\n" +
            "            \"countyName\": \"string\",\n" +
            "            \"electricityCost\": \"string\",\n" +
            "            \"elevator\": true,\n" +
            "            \"floor\": \"string\",\n" +
            "            \"floorCount\": \"string\",\n" +
            "            \"gasCost\": \"string\",\n" +
            "            \"houseSpace\": \"string\",\n" +
            "            \"houseType\": \"string\",\n" +
            "            \"houseTypeName\": \"string\",\n" +
            "            \"householdNumber\": \"string\",\n" +
            "            \"leaseStatus\": \"string\",\n" +
            "            \"leaseStatusName\": \"string\",\n" +
            "            \"leaseType\": \"string\",\n" +
            "            \"leaseTypeName\": \"string\",\n" +
            "            \"maintenanceCost\": \"string\",\n" +
            "            \"name\": \"string\",\n" +
            "            \"note\": \"string\",\n" +
            "            \"parlourNum\": \"string\",\n" +
            "            \"provName\": \"string\",\n" +
            "            \"roomCode\": \"string\",\n" +
            "            \"roomNo\": \"string\",\n" +
            "            \"roomNum\": \"string\",\n" +
            "            \"roomSpace\": \"string\",\n" +
            "            \"thirdNo\": \"string\",\n" +
            "            \"toiletNum\": \"string\",\n" +
            "            \"unitNo\": \"string\",\n" +
            "            \"waterCost\": \"string\"\n" +
            "        }\n" +
            "    ]\n" +
            "}";



    private static String publickey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC4W7KA25AvgoMjwnxo+cdNhTASbEGQOQkgulOac1oS4YHL9eb8QFkx++CZffU21UqgIUpAo8qR1wf6/3j8TECnU147671rARINJWLl4Oj4KRInt5FTjBdHC6M6NBQBpQv6xFrtlq8UGORE9LS4HnPoKGoKiIyTPEO18ilsNkHDuwIDAQAB";
    private static String privatekey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALhbsoDbkC+CgyPCfGj5x02FMBJsQZA5CSC6U5pzWhLhgcv15vxAWTH74Jl99TbVSqAhSkCjypHXB/r/ePxMQKdTXjvrvWsBEg0lYuXg6PgpEie3kVOMF0cLozo0FAGlC/rEWu2WrxQY5ET0tLgec+goagqIjJM8Q7XyKWw2QcO7AgMBAAECgYBujibEmWak5wpHdoju1uMK/NTwJ1VF7L4pjzM1ivk4G12f903WdaxHijmNMnjLsiq3Tl9d1htTS/UwMuXVvtCBwWelrTxihNQzcNgS1NNps4fXS6RfhTfGLJLi2tb3AjMBaOgtGosl7GTcJwNewbJr4fv/ElMeH+YSc8Lw79MVOQJBAO/unjJNqjIN/dzcASuLZlV+g7q6GIZp1kqonvySqBJJJtMIpXi7+0uAuxY6id37qz7crqEzRyGuJj8pZnYaf28CQQDEtFLNlVpCkrD2eAfsQYYvxsTNGmFsgmhURO0i8uS4e4ey6JntDh6l75u+a1f6nugh1saoIYDfTPMK7yj979p1AkBRHQ6GWxNK0MgePpJ2si3qgVbvbbKU3nr/ynnVUY9YfzqM5cNrScHvCJo3LZsmXMrL+bdf8AIANOvhNpHZI6QpAkEAlxwYCErV/hKG07C+FWb42LcP9Khxc1RzJVmV+qUxw+9R/cTmis+wB7WcpZn9ClEM7wH5tquWPvT0ONAlY37hCQJAGQ1ITivxyRperaXnHrUcw2b874b0I9jaWpJqh2aGgeHX5KWRQdHJvtEPJVT4lWjXylm5jZb2AGqsgGVK2Bc/tA==";
    public static void main(String[] args) {
        String objStr = "{\n" +
                "            \"address\": \"string\",\n" +
                "            \"airConditioningCost\": \"string\",\n" +
                "            \"alias\": \"string\",\n" +
                "            \"buildType\": \"string\",\n" +
                "            \"buildTypeName\": \"string\",\n" +
                "            \"buildingCode\": \"string\",\n" +
                "            \"cityName\": \"string\",\n" +
                "            \"countyName\": \"string\",\n" +
                "            \"electricityCost\": \"string\",\n" +
                "            \"elevator\": true,\n" +
                "            \"floor\": \"string\",\n" +
                "            \"floorCount\": \"string\",\n" +
                "            \"gasCost\": \"string\",\n" +
                "            \"houseSpace\": \"string\",\n" +
                "            \"houseType\": \"string\",\n" +
                "            \"houseTypeName\": \"string\",\n" +
                "            \"householdNumber\": \"string\",\n" +
                "            \"leaseStatus\": \"string\",\n" +
                "            \"leaseStatusName\": \"string\",\n" +
                "            \"leaseType\": \"string\",\n" +
                "            \"leaseTypeName\": \"string\",\n" +
                "            \"maintenanceCost\": \"string\",\n" +
                "            \"name\": \"string\",\n" +
                "            \"note\": \"string\",\n" +
                "            \"parlourNum\": \"string\",\n" +
                "            \"provName\": \"string\",\n" +
                "            \"roomCode\": \"string\",\n" +
                "            \"roomNo\": \"string\",\n" +
                "            \"roomNum\": \"string\",\n" +
                "            \"roomSpace\": \"string\",\n" +
                "            \"thirdNo\": \"string\",\n" +
                "            \"toiletNum\": \"string\",\n" +
                "            \"unitNo\": \"string\",\n" +
                "            \"waterCost\": \"string\"\n" +
                "        }";
        Map<String,Object> obj = MapUtil.str2Map(objStr);
        Map<String,Object> map = MapUtil.str2Map(str);
        List list = (List) map.get("data");
        for (int i=0;i<=50;i++){
            list.add(obj);
        }
        //数据加密
        Map<String, String> reqParams = decode(map);

        String result = HttpClient.conHttpByJson("http://open.lezujiayuan.com/service-supervision/scrt/data/house/batch",reqParams,null);
        System.out.println(result);

    }

    private static Map<String, String> decode(Map<String, Object> map) {
        EncryptManager encryptManager = new EncryptManager(privatekey,publickey);
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("mobKey",encryptManager.getMobKey());
        map.forEach((k,v)->{
            if(k.equals("data")){
                byte[] bytes =objectToByteArray(v);
                String val = Base64Utils.encodeToString(bytes);
                reqParams.put(k,encryptManager.encryptStr(val));//对 data 加密
            }else{
                reqParams.put(k, (String) v);
            }
        });

        SignUtils.getSign(reqParams,privatekey);//加签
        return reqParams;
    }

    public static byte[] objectToByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();
            bytes = byteArrayOutputStream.toByteArray();
        } catch (IOException var17) {
        } finally {
            if(objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException var16) {
                    var16.printStackTrace();
                }
            }

            if(byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException var15) {
                    var15.printStackTrace();
                }
            }

        }

        return bytes;
    }

}
