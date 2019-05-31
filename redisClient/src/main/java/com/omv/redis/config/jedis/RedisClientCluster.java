/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.omv.redis.config.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.Collections;
import java.util.Set;

/**
 * JedisClientCluster.
 *
 * @author xiaoyu(Myth)
 */
public class RedisClientCluster implements RedisClient {
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Long RELEASE_SUCCESS = 1L;

    private JedisCluster jedisCluster;

    public RedisClientCluster(final JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    @Override
    public boolean exists(String key) {
        boolean flag = false;
        flag = jedisCluster.exists(key);
        return flag;
    }

    @Override
    public String set(final String key, final String value) {
        return jedisCluster.set(key, value);
    }

    @Override
    public String set(String key, String value, int time) {
        String result = set(key, value);
        expire(key, time);
        return result;
    }

    @Override
    public String set(final String key, final byte[] value) {
        return jedisCluster.set(key.getBytes(), value);
    }

    @Override
    public Long del(final String... keys) {
        return jedisCluster.del(keys);
    }

    @Override
    public String get(final String key) {
        return jedisCluster.get(key);
    }

    @Override
    public byte[] get(final byte[] key) {
        return jedisCluster.get(key);
    }

    @Override
    public Set<byte[]> keys(final byte[] pattern) {
        return jedisCluster.hkeys(pattern);
    }

    @Override
    public Set<String> keys(final String key) {
        return jedisCluster.hkeys(key+"*");
    }

    @Override
    public Long hset(final String key, final String item, final String value) {
        return jedisCluster.hset(key, item, value);
    }

    @Override
    public String hget(final String key, final String item) {
        return jedisCluster.hget(key, item);
    }

    @Override
    public Long hdel(final String key, final String item) {
        return jedisCluster.hdel(key, item);
    }

    @Override
    public Long incr(final String key) {
        return jedisCluster.incr(key);
    }

    @Override
    public Long decr(final String key) {
        return jedisCluster.decr(key);
    }

    @Override
    public Long expire(final String key, final int second) {
        return jedisCluster.expire(key, second);
    }

    @Override
    public Set<String> zrange(final String key, final long start, final long end) {
        return jedisCluster.zrange(key, start, end);
    }

    @Override
    public Boolean distributedLock(String lockKey, String requestId, int expireTime) {
        String result = jedisCluster.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
        if (LOCK_SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean releaseDistributedLock(String lockKey, String requestId) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedisCluster.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));

        if (RELEASE_SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }

}
