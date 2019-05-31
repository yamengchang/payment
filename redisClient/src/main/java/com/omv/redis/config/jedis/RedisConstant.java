package com.omv.redis.config.jedis;

/**
 * Created by zwj on 2018/8/9.
 */
public class RedisConstant {
    public static final String LOCK_SUCCESS = "OK";
    public static final String SET_IF_NOT_EXIST = "NX";
    public static final String SET_WITH_EXPIRE_TIME = "PX";
    public static final Long RELEASE_SUCCESS = 1L;
}
