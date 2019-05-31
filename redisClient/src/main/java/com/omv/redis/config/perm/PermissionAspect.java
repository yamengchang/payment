package com.omv.redis.config.perm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.omv.common.util.basic.HttpServletUtil;
import com.omv.common.util.basic.ValueUtil;
import com.omv.common.util.userutil.UserUtils;
import com.omv.redis.config.jedis.RedisClient;
import com.omv.redis.config.utils.RedisConstants;
import com.omv.redis.config.utils.UserInfoUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * Created by by on 2017/8/3.
 */
@Aspect
@Component
public class PermissionAspect {

    private static final Logger logger = LoggerFactory.getLogger(PermissionAspect.class);

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private UserInfoUtils userInfoUtils;

    @Value("${omv.permission.valid.enable:false}")
    private Boolean isEnabled;

    //Controller层切点 包路径
    @Pointcut("execution (* com.*.controller..*(..))")
    public void controllerAspect() {
    }

    private String getOperation(String requestPerm, String userInfo) {
        String userPerms = ValueUtil.getFromJson(userInfo, "allPerms");
        JSONObject jsonObject = JSON.parseObject(userPerms);
        return jsonObject.getString(requestPerm);
    }

    /**
     * 前置通知 用于拦截Controller层记录用户的操作
     *
     * @param joinPoint 切点
     */
    @Before("controllerAspect()")
    public void doBefore(JoinPoint joinPoint) throws ClassNotFoundException, NoSuchMethodException {
        if (!getEnabled()) {
            return;
        }
        HttpServletRequest request = HttpServletUtil.getRequests();
        String initiator = request.getHeader("initiator");
        if (null != initiator && initiator.equals("feign")) {
            return;
        }
        //得到被代理的方法
        Signature signature = joinPoint.getSignature();
        if (!(signature instanceof MethodSignature)) {
            throw new IllegalArgumentException();
        }
        //获取方法的参数名和参数值
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = joinPoint.getTarget().getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        //得到被代理的方法上的注解
        Object annotation = method.getAnnotation(PermissionValid.class);
        if (annotation != null) {
            String[] values = method.getAnnotation(PermissionValid.class).value();
            String permInfo = redisClient.get(RedisConstants.PERM_PREFIX + UserUtils.getUserId());
            Boolean isAllPerm = userInfoUtils.getUserInfo().getBoolean("isAllPerm");
            boolean flag = false;
            if (null == isAllPerm || !isAllPerm) {
                for (String value : values) {
                    if (StringUtils.isNotEmpty(permInfo) && StringUtils.contains(permInfo, value)) {
                        flag = true;
                        return;
                    }
                }

                if (!flag) {
                    ValueUtil.isError("900", "permission is not found");
                }
            }
        }

        if (logger.isInfoEnabled()) {
            logger.info("before " + joinPoint);
        }
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }
}
