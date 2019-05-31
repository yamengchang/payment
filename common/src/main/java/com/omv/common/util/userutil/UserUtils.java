package com.omv.common.util.userutil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.omv.common.util.basic.HttpServletUtil;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zwj on 2018/3/2.
 */
public class UserUtils {
    public static String getUserName() {
        String claims = getClaims();
        System.out.println(claims);
        if (claims == null) {
            return null;
        } else {
            JSONObject jsonObject = JSON.parseObject(claims);
            String userName = jsonObject.getString("user_name");
            System.out.println(claims);
            return userName;
        }
    }

    public static String getUserId() {
        String claims = getClaims();
        if (claims == null) {
            return null;
        } else {
            JSONObject jsonObject = JSON.parseObject(claims);
            String userId = jsonObject.getString("user_id");
            return userId;
        }
    }

    public static String getUserId(String token) {
        Jwt var8 = JwtHelper.decode(token);
        String claims = var8.getClaims();
        if (claims == null) {
            return null;
        } else {
            JSONObject jsonObject = JSON.parseObject(claims);
            String userId = jsonObject.getString("user_id");
            return userId;
        }
    }


    public static List<String> getUserRoles() {
        String claims = getClaims();
        JSONObject jsonObject = JSON.parseObject(claims);
        String[] authorities = (String[]) jsonObject.getJSONArray("authorities").toArray(new String[0]);
        ArrayList roles = new ArrayList();
        String[] var4 = authorities;
        int var5 = authorities.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            String role = var4[var6];
            role = role.substring(5);
            roles.add(role);
        }

        return roles;
    }

    /*
    *@Author : Gavin
    *@Email : gavinsjq@sina.com
    *@Date: 2018/5/2 18:56
    *@Description : 是否是管理员
    *@Params :  * @param null
    */
    public static boolean isAdmin() {
        String claims = getClaims();
        if (claims == null) {
            return false;
        } else {
            JSONObject dataJson = JSON.parseObject(claims);
            String prefix = dataJson.getString("user_type");
            if (prefix.equals("9")) {
                return true;
            } else {
                return false;
            }
        }
    }


    private static String getClaims() {
        HttpServletRequest request = HttpServletUtil.getRequests();
        String tokenString = request.getHeader("Authorization");
        if (tokenString == null || "".equals(tokenString)) {
            tokenString = request.getParameter("token");
            if (tokenString == null || "".equals(tokenString)) {
                Cookie[] jwt = request.getCookies();
                if (null != jwt) {
                    Cookie[] claims = jwt;
                    int var4 = jwt.length;

                    for (int var5 = 0; var5 < var4; ++var5) {
                        Cookie cookie = claims[var5];
                        String name = cookie.getName();
                        if ("COSCON_ACCESS_TOKEN".equals(name)) {
                            tokenString = "bearer " + cookie.getValue();
                        }
                    }
                }
            }
        }

        if (null == tokenString) {
            return null;
        } else {
            int first = tokenString.indexOf("bearer ");
            if (first >= 0) {
                tokenString = tokenString.replace("bearer ", "");
            }
            Jwt var8 = JwtHelper.decode(tokenString);
            String var9 = var8.getClaims();
            return var9;
        }
    }

}
