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

import com.omv.common.util.basic.SerializeUtil;
import org.springframework.stereotype.*;
import redis.clients.jedis.*;

import java.util.*;


/**
 * JedisClientSingle.
 * @author xiaoyu(Myth)
 */
public class RedisClientSingle implements RedisClient {

	private JedisPool jedisPool;

	public RedisClientSingle(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	@Override
	public boolean exists(String key) {
		boolean flag = false;
		try (Jedis jedis = jedisPool.getResource()) {
			String result = get(key);
			if (null != result) {
				flag = true;
			}
		}
		return flag;
	}

	@Override
	public String set(final String key, final String value) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.set(key, value);
		}
	}

	@Override
	public String set(String key, String value, int time) {
		String result = set(key, value);
		expire(key, time);
		return result;
	}

	@Override
	public String set(final String key, final byte[] value) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.set(key.getBytes(), value);
		}
	}

	@Override
	public Long del(final String... keys) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.del(keys);
		}
	}

	@Override
	public String get(final String key) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.get(key);
		}
	}

	@Override
	public byte[] get(final byte[] key) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.get(key);
		}
	}

	@Override
	public Set<byte[]> keys(final byte[] pattern) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.keys(pattern);
		}
	}

	@Override
	public Set<String> keys(final String key) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.keys(key+"*");
		}
	}

	@Override
	public Long hset(final String key, final String item, final String value) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.hset(key, item, value);
		}
	}

	@Override
	public String hget(final String key, final String item) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.hget(key, item);
		}
	}

	@Override
	public Long hdel(final String key, final String item) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.hdel(key, item);
		}
	}

	@Override
	public Long incr(final String key) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.incr(key);
		}
	}

	@Override
	public Long decr(final String key) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.decr(key);
		}
	}

	@Override
	public Long expire(final String key, final int second) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.expire(key, second);
		}
	}

	@Override
	public Set<String> zrange(final String key, final long start, final long end) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.zrange(key, start, end);
		}
	}

	@Override
	public Boolean distributedLock(String lockKey, String requestId, int expireTime) {
		try (Jedis jedis = jedisPool.getResource()) {
			String result = jedis.set(lockKey, requestId, RedisConstant.SET_IF_NOT_EXIST, RedisConstant.SET_WITH_EXPIRE_TIME, expireTime);
			if (RedisConstant.LOCK_SUCCESS.equals(result)) {
				return true;
			}
			return false;
		}
	}

	@Override
	public Boolean releaseDistributedLock(String lockKey, String requestId) {
		try (Jedis jedis = jedisPool.getResource()) {
			String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
			Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));

			if (RedisConstant.RELEASE_SUCCESS.equals(result)) {
				return true;
			}
			return false;
		}
	}
}
