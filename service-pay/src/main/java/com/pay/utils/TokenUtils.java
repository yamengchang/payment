package com.pay.utils;

import com.omv.redis.config.jedis.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.util.*;

import java.util.*;

@Component
public class TokenUtils {

	@Autowired
	private RedisConfig redisConfig;
	private Integer EXPIRED = 60 * 60 * 24;
	private Integer EXPIRED_TIME = 60 * 30;

	public String generatorToken(String userName) {
		// 创建 GUID 对象
		UUID uuid = UUID.randomUUID(); // 得到对象产生的ID
		String token = uuid.toString(); // 转换为大写
		token = token.toUpperCase(); // 替换 “-”变成空格
		token = token.replaceAll("-", "");
		RedisClient redisClient = redisConfig.defaultRedisConfig();
		redisClient.set(token, userName, EXPIRED);
		return token;
	}

	public String generator(String value) {
		// 创建 GUID 对象
		UUID uuid = UUID.randomUUID(); // 得到对象产生的ID
		String token = uuid.toString(); // 转换为大写
		token = token.toUpperCase(); // 替换 “-”变成空格
		token = token.replaceAll("-", "");
		RedisClient redisClient = redisConfig.defaultRedisConfig();
		redisClient.set(token, value, EXPIRED_TIME);
		return token;
	}

	public String getValue(String name) {
		Assert.notNull(name, "ACCESS_TOKEN 不能为空");
		RedisClient redisClient = redisConfig.defaultRedisConfig();
		String token = redisClient.get(name);
		//		Assert.notNull(token, "无效的请求");
		return token;
	}
}
