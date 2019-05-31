package com.omv.redis.config.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.omv.common.util.basic.HttpServletUtil;
import com.omv.common.util.basic.ValueUtil;
import com.omv.common.util.userutil.UserUtils;
import com.omv.redis.config.jedis.RedisClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zwj on 2018/3/2.
 */
@Component
public class UserInfoUtils {
    @Autowired
    private RedisClient redisClient;

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

    public boolean checkPerm(String permValue) {
        String permInfo = redisClient.get(RedisConstants.PERM_PREFIX + getUserId());
        Boolean isAllPerm = getUserInfo().getBoolean("isAllPerm");
        if (null != isAllPerm && !isAllPerm) {
            if (StringUtils.isNotEmpty(permInfo) && !(permInfo.indexOf(permValue) >= 0)) {
                return false;
            }
        }
        return true;
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


    public JSONObject getUserInfo() {
        String userId = getUserId();
        String userInfo = redisClient.get(RedisConstants.USER_INFO + userId);
        return JSON.parseObject(userInfo);
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
