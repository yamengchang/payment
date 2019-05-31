package com.omv.redis.config.cache;

import java.lang.annotation.*;

/*
*@Author : Gavin
*@Email : gavinsjq@sina.com
*@Date: 2018/8/7 16:21
*@Description :
*@Params :  * @param null
*/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface RedisCache {

    String cacheKey() default "";

    /**
     * @Description: 数据缓存时间单位s秒
     * @Param: 默认10分钟
     * @return:
     * @Author:
     * @Date: 2018/5/16
     */
    int cacheTime() default 600;

    RedisCacheEnum operation() default RedisCacheEnum.CREATE;

}
