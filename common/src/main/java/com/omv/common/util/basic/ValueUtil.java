
package com.omv.common.util.basic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.omv.common.util.error.CustomException;
import com.omv.common.util.properties.PropertiesUtil;
import com.google.gson.*;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/*
 *@Author:Gavin
 *@Email:gavinsjq@sina.com
 *@Date:  2017/12/12
 *@Param
 *@Description:
 */
@Component
public class ValueUtil {

    public static String defaultSuccess = "success";

    public static boolean notEmpity(Object obj) {
        if (null == obj) {
            return false;
        } else if (obj.toString().equals("")) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isEmpity(Object obj) {
        return !notEmpity(obj);
    }


    public static <T> T coalesce(T... args) {
        for (int i = 0; i < args.length; i++) {
            if (notEmpity(args[i])) {
                return args[i];
            }
        }
        return (T) "";
    }

    public static Gson gsonExp = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").excludeFieldsWithoutExposeAnnotation().create();
    //    public static Gson gson = new Gson();
    public static Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

//    public static String toJson(String code, Object obj) {
//        RestJson restJson = new RestJson();
////        restJson.setCode(code);
//        restJson.setMsg(defaultSuccess);
//        restJson.setData(coalesce(obj, ""));
//        return gson.toJson(restJson);
//    }

    public static String toJson(Integer code, Object obj) {
        RestJson restJson = new RestJson();
        restJson.setCode(String.valueOf(code));
        restJson.setMsg(defaultSuccess);
        restJson.setData(coalesce(obj, ""));
        return gson.toJson(restJson);
    }

    public static String toJson(Integer code, String msg, Object obj) {
        RestJson restJson = new RestJson();
        restJson.setCode(String.valueOf(code));
        restJson.setMsg(getMsg(msg));
        restJson.setData(coalesce(obj, ""));
        return gson.toJson(restJson);
    }

    public static void checkResult(String result) throws CustomException {
        String code;
        String msg = "";
        try {
            JSONObject json = JSON.parseObject(result);
            code = json.getString("code");
            msg = json.getString("msg");
            if (null == code || !code.equals("200")) {
                isError("远程调用失败:" + msg);
            }
        } catch (Exception e) {
            isError("远程调用失败:" + msg);
        }

    }

    public static String toJson(Object obj) {
        try {
            RestJson restJson = new RestJson();
            restJson.setCode(String.valueOf(HttpStatus.SC_OK));
            restJson.setMsg(defaultSuccess);
            Map<String, Object> dataContent = new HashMap<>();
            if (obj == null) {
                return gson.toJson(restJson);
            }
            //判断是否存在 addCondition 方法
            try {
                Method method = obj.getClass().getMethod("getPage");
            } catch (NoSuchMethodException ex) {
                dataContent.put("content", coalesce(obj, ""));
                obj = dataContent;
            }
            restJson.setData(obj);
//            JSONValue jsonValue = JSONMapper.toJSON(restJson);
//            String jsonStr = jsonValue.render(false);
//            return jsonStr;
            return gson.toJson(restJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject toJsonObject(Object obj) {
        try {
            return JSON.parseObject(gson.toJson(coalesce(obj, "")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toJson() {
        try {
            RestJson restJson = new RestJson();
            restJson.setCode(String.valueOf(HttpStatus.SC_OK));
            restJson.setMsg(defaultSuccess);
            restJson.setData("");
            return gson.toJson(restJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toString(Object obj) {
        return gson.toJson(obj);
    }


    public static String toJson(Object... objs) {
        RestJson restJson = toRestJson(objs);
        return gson.toJson(restJson);
    }

    public static String toJsonExp(Object... objs) {
        RestJson restJson = toRestJson(objs);
        return gsonExp.toJson(restJson);
    }


    public static String toError(String code, String message) {
        RestJson restJson = new RestJson();
        restJson.setCode(code);
        restJson.setData("");
        restJson.setMsg(getMsg(message));
        return toString(restJson);
    }

    public static String toError(String code, String message, Object object) {
        RestJson restJson = toRestJson(object);
        restJson.setCode(code);
        restJson.setData(object);
        restJson.setMsg(getMsg(message));
        return gson.toJson(restJson);
    }

    public static String toError(Integer code, String message) {
        RestJson restJson = new RestJson();
        restJson.setCode(String.valueOf(code));
        restJson.setMsg(getMsg(message));
        restJson.setData("");
        return toString(restJson);
    }

    public static RestJson toRestJson(Object... objs) {
        RestJson restJson = new RestJson();
        restJson.setCode(String.valueOf(HttpStatus.SC_OK));
        restJson.setMsg("");
        Map<String, Object> map = new HashMap<>();
        boolean isOdd = true;
        String key = "";
        for (int i = 0; i < objs.length; i++) {
            if (isOdd) {
                key = objs[i].toString();
                isOdd = false;
            } else {
                if (notEmpity(objs[i])) {
                    map.put(key, objs[i]);
                } else {
                    map.put(key, new JsonObject());
                }
                isOdd = true;
            }
        }
        restJson.setData(map);
        return restJson;
    }


    private static JsonParser jsonParser = new JsonParser();

    public static JsonElement getFromJson(String json) {
        JsonElement origin = jsonParser.parse(json);
        return origin;
    }

    public static <T> T getFromJson(String json, Class<T> clz) {
        return gson.fromJson(json, clz);
    }

    public static String getFromJson(String json, Object... args) {
        JsonObject origin = jsonParser.parse(json).getAsJsonObject();
        for (int i = 0; i < args.length; i++) {
            if ((i + 1) == args.length) {
                String returnString = origin.get(args[i].toString()).toString();
                if (returnString.startsWith("\"")) {
                    return returnString.substring(1, returnString.length() - 1);
                } else {
                    return returnString;
                }
            } else {
                if (args[i].getClass().equals(Integer.class)) {
                    origin = origin.getAsJsonArray().get(Integer.valueOf(args[i].toString())).getAsJsonObject();
                }
                if (args[i].getClass().equals(String.class)) {
                    origin = origin.getAsJsonObject(args[i].toString());
                }
            }
        }
        return origin.toString();
    }


    public static void verify(Object param, String errorCode, String errorMsg) throws CustomException {
        if (isEmpity(param)) {
            throw new CustomException(errorCode, errorMsg);
        }
    }


    public static void verifyParams(String params, Object... objs) throws CustomException {
        String[] paramArr = params.split(",");
        int objSize = 0;
        if (objs instanceof String[]) {
            objSize = 1;
        } else {
            for (Object obj : objs) {
                objSize++;
            }
        }
        if (paramArr.length != objSize) {
            throw new CustomException("720", "后台校验参数数量错误");
        }
        String nullParam = "";
        for (int i = 0; i < objs.length; i++) {
            Object objStr = objs[i];
            if (isEmpity(objStr)) {
                nullParam += (paramArr[i] + "为空 , ");
            }
        }
        if (!"".equals(nullParam)) {
            throw new CustomException("720", nullParam.substring(0, nullParam.lastIndexOf(",") - 1));
        }
    }

    public static void verifyParams(String params, Object obj) throws CustomException {
        try {
            String[] paramArr = params.split(",");
            Class clazz = obj.getClass();
            String errMsg = "";
            if (obj instanceof Map) {
                Map map = (Map) obj;
                for (String param : paramArr) {
                    Object value = map.get(param);
                    if (null == value || StringUtils.isEmpty(String.valueOf(value))) {
                        errMsg += param + "为空;";
                    }
                }
            } else if (clazz.getTypeName().indexOf("java.lang") >= 0) {
                if (null == obj || StringUtils.isEmpty(String.valueOf(obj))) {
                    errMsg += paramArr[0] + "为空;";
                }
            } else {
                for (String param : paramArr) {
                    Field field = null;
                    field = clazz.getDeclaredField(param);
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    if (StringUtils.isEmpty(String.valueOf(value))) {
                        errMsg += param + "为空;";
                    }
                }
            }
            if (StringUtils.isNotEmpty(errMsg)) {
                isError("720", errMsg.substring(0, errMsg.length() - 1));
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void isError(String message) throws CustomException {
        throw new CustomException("710", getMsg(message));
    }

    public static void isError(String errorCode, String message) throws CustomException {
        throw new CustomException(errorCode, getMsg(message));
    }

    public static String getMsg(String message) {
        boolean useMsgFile = false;
        if (!useMsgFile) {
            return message;
        }
        String language = LanguageUtil.getLanguage().replace("-", "_");
        String msg = null;
        try {
            msg = PropertiesUtil.getString(message, "messages_" + language + ".properties");
        } catch (Exception e) {
            return message;
        }
        if (msg == null) {
            msg = message;
        }
        return msg;
    }
}

