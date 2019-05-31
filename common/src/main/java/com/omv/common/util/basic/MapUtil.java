package com.omv.common.util.basic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
*@Author:Gavin
*@Email:gavinsjq@sina.com
*@Date:  2017/12/12
*@Param
*@Description:
*/
public class MapUtil {

    public static Map<String, Object> str2Map(String data) {
        Gson gson = new Gson();
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map = gson.fromJson(data, map.getClass());
        return map;
    }

    public static Map[] jsonArrayToMap(String jsonArray) {
        JSONArray json = JSON.parseArray(jsonArray);
        Map[] maps = new HashMap[json.size()];

        for (int i = 0; i < json.size(); i++) {
            JSONObject jsonObject = (JSONObject) json.get(i);
            maps[i] = objectToMap(jsonObject);
        }
        return maps;
    }

    public static Map<String, Object> strToMap(String str) {
        JSONObject jsonObject = JSON.parseObject(str);
        return (Map<String, Object>) jsonObject;
    }

    public static Map<String, Object> objectToMap(Object entity) {
        Map<String, Object> map = new HashMap<>();
        if (null == entity) {
            return null;
        }
        Field[] fields = entity.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(entity));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static Map<String, String> objectToStrMap(Object entity) {
        Map<String, String> map = new HashMap<>();
        if (null == entity) {
            return null;
        }
        Field[] fields = entity.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(entity) == null ? "" : field.get(entity).toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static Object mapToObject(Map<String, Object> map, Class classs) {
        if (null == map) {
            return null;
        }
        Field[] fields = classs.getDeclaredFields();
        try {
            Object obj = classs.newInstance();

            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                String setterName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                Method setter = null;
                Class fieldType = field.getType();

                setter = classs.getMethod(setterName, fieldType);
                String a = String.valueOf(fieldType);
                if (null == map.get(field.getName())) {
                    continue;
                }
                switch (a) {
                    case "class java.lang.String":
                        setter.invoke(obj, String.valueOf(map.get(field.getName())));
                        break;
                    case "class java.lang.Integer":
                        setter.invoke(obj, Integer.valueOf(String.valueOf(map.get(field.getName()))));
                        break;
                    case "class java.lang.Boolean":
                        setter.invoke(obj, Boolean.valueOf(String.valueOf(map.get(field.getName()))));
                        break;
                    case "class java.lang.Long":
                        setter.invoke(obj, Long.valueOf(String.valueOf(map.get(field.getName()))));
                        break;
                    case "class java.lang.Double":
                        setter.invoke(obj, Double.valueOf(String.valueOf(map.get(field.getName()))));
                        break;
                    case "class java.lang.Float":
                        setter.invoke(obj, Float.valueOf(String.valueOf(map.get(field.getName()))));
                        break;
                    case "class java.math.BigInteger":
                        setter.invoke(obj, BigInteger.valueOf(Long.parseLong(String.valueOf(map.get(field.getName())))));
                        break;
                    case "class java.math.BigDecimal":
                        setter.invoke(obj, BigDecimal.valueOf(Long.parseLong(String.valueOf(map.get(field.getName())))));
                        break;
//                    case "class java.util.Date":
//                        setter.invoke(obj, java.util.Date.valueOf( String.valueOf(map.get(field.getName()))));
//                        break;
                    case "class java.sql.Date":
                        setter.invoke(obj, Date.valueOf(String.valueOf(map.get(field.getName()))));
                        break;
                    case "class java.sql.Time":
                        setter.invoke(obj, Time.valueOf(String.valueOf(map.get(field.getName()))));
                        break;
                    case "class java.sql.Timestamp":
                        setter.invoke(obj, Timestamp.valueOf(String.valueOf(map.get(field.getName()))));
                        break;
                    default:
                        System.out.println();
                }
            }
            return obj;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Map<String, String> getParameter(Map<String, String[]> maps) {
        Map<String, String> out = new HashMap<>();
        for (Map.Entry<String, String[]> set : maps.entrySet()) {
            out.put(set.getKey(), set.getValue()[0]);
        }
        return out;
    }

    public static void cleanNull(Map map) {
        Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (null == value || value.equals("")) {
                it.remove();
            }
        }
    }

    public static void cleanNullAndTimestamp(Map map) {
        Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (null == value || value.equals("") || key.equals("timestamp")) {
                it.remove();
            }
        }
    }

    public static <T> T test(Class<T> clazz, T t, Map<String, Object> map) {
        T obj = null;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            obj = clazz.newInstance(); // 创建 JavaBean 对象
            for (Map.Entry entry : map.entrySet()) {
                String key = (String) entry.getKey();
                Object value = entry.getValue();
//                clazz.getClass().getMethod("set"+key)
            }

//            Method after = clazz.getMethod("afterFind", String.class);


            // 给 JavaBean 对象的属性赋值

            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                String propertyName = descriptor.getName();
                if (map.containsKey(propertyName)) {
                    // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
                    Object value = map.get(propertyName);
                    if ("".equals(value)) {
                        value = null;
                    }
                    Object[] args = new Object[1];
                    args[0] = value;
                    try {
                        descriptor.getWriteMethod().invoke(obj, args);
                    } catch (InvocationTargetException e) {
                        System.out.println("字段映射失败");
                    }
                }
            }
        } catch (IllegalAccessException e) {
            System.out.println("实例化 JavaBean 失败");
        } catch (IntrospectionException e) {
            System.out.println("分析类属性失败");
        } catch (IllegalArgumentException e) {
            System.out.println("映射错误");
        } catch (InstantiationException e) {
            System.out.println("实例化 JavaBean 失败");
        }

        return (T) obj;
    }


    /*
    *@Author Gavin
    *@Description 判断字符串是否为数字
    *@Date 2017/3/20 16:49
    *@Email gavinsjq@sina.com
    *@Params
    */
    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 字符串是否为数值
     *
     * @return
     * @params string
     */
    public static boolean isNum(String string) {
        if (string == null || "".equals(string)) {
            return false;
        }
        for (int i = 0; i < string.length(); i++) {
            if (!Character.isDigit(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    public static <T> T toBean(Class<T> clazz, Map map) {
        T obj = null;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            obj = clazz.newInstance(); // 创建 JavaBean 对象

            // 给 JavaBean 对象的属性赋值
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                String propertyName = descriptor.getName();
                if (map.containsKey(propertyName)) {
                    // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
                    Object value = map.get(propertyName);
                    if ("".equals(value)) {
                        value = null;
                    }
                    Object[] args = new Object[1];
                    args[0] = value;
                    try {
                        descriptor.getWriteMethod().invoke(obj, args);
                    } catch (InvocationTargetException e) {
                        System.out.println("字段映射失败");
                    }
                }
            }
        } catch (IllegalAccessException e) {
            System.out.println("实例化 JavaBean 失败");
        } catch (IntrospectionException e) {
            System.out.println("分析类属性失败");
        } catch (IllegalArgumentException e) {
            System.out.println("映射错误");
        } catch (InstantiationException e) {
            System.out.println("实例化 JavaBean 失败");
        }
        return (T) obj;
    }


}
