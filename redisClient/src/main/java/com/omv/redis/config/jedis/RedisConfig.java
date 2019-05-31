package com.omv.redis.config.jedis;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zwj on 2018/8/2.
 */
@Component
public class RedisConfig {
	private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

	@Autowired
	RedisProperties redisProperties;

	public RedisClient defaultRedisConfig() {
		try {
			return buildJedisPool(redisProperties);
		}catch (Exception e) {
			logger.error("redis 初始化异常！请检查配置信息:{}", e.getMessage());
		}
		return null;
	}

	@Bean
	public RedisClient initRedisConfig(RedisProperties jedisProperties) {
		try {
			return buildJedisPool(jedisProperties);
		}catch (Exception e) {
			logger.error("redis 初始化异常！请检查配置信息:{}", e.getMessage());
		}
		return null;
	}

	private RedisClient buildJedisPool(final RedisProperties jedisProperties) {
		RedisClient jedisClient;
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxIdle(jedisProperties.getMaxIdle());
		//最小空闲连接数, 默认0
		config.setMinIdle(jedisProperties.getMinIdle());
		//最大连接数, 默认8个
		config.setMaxTotal(jedisProperties.getMaxTotal());
		//获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
		config.setMaxWaitMillis(jedisProperties.getMaxWaitMillis());
		//在获取连接的时候检查有效性, 默认false
		config.setTestOnBorrow(jedisProperties.getTestOnBorrow());
		//返回一个jedis实例给连接池时，是否检查连接可用性（ping()）
		config.setTestOnReturn(jedisProperties.getTestOnReturn());
		//在空闲时检查有效性, 默认false
		config.setTestWhileIdle(jedisProperties.getTestWhileIdle());
		//逐出连接的最小空闲时间 默认1800000毫秒(30分钟 )
		config.setMinEvictableIdleTimeMillis(jedisProperties.getMinEvictableIdleTimeMillis());
		//对象空闲多久后逐出, 当空闲时间>该值 ，且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断  (默认逐出策略)，默认30m
		config.setSoftMinEvictableIdleTimeMillis(jedisProperties.getSoftMinEvictableIdleTimeMillis());
		//逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
		config.setTimeBetweenEvictionRunsMillis(jedisProperties.getTimeBetweenEvictionRunsMillis());
		//每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
		config.setNumTestsPerEvictionRun(jedisProperties.getNumTestsPerEvictionRun());

		JedisPool jedisPool;
		//如果是集群模式
		if (jedisProperties.getCluster()) {
			logger.info("=== 构造redis集群模式 ===");
			final String clusterUrl = jedisProperties.getClusterUrl();
			String[] addresses = clusterUrl.split(";");
			Set<HostAndPort> hostAndPorts = new HashSet<>();
			for (String address : addresses) {
				String host = address.split(":")[0];
				String port = address.split(":")[1];
				HostAndPort hostAndPort = new HostAndPort(host, Integer.valueOf(port));
				hostAndPorts.add(hostAndPort);
			}
			JedisCluster jedisCluster = new JedisCluster(hostAndPorts, config);
			jedisClient = new RedisClientCluster(jedisCluster);
		}else {
			logger.info("==== 构造redis单机模式 ====");
			if (StringUtils.isNoneBlank(jedisProperties.getPassword())) {
				jedisPool = new JedisPool(config, jedisProperties.getHostName(), jedisProperties.getPort(), jedisProperties.getTimeOut(), jedisProperties.getPassword());
			}else {
				jedisPool = new JedisPool(config, jedisProperties.getHostName(), jedisProperties.getPort(), jedisProperties.getTimeOut());
			}
			jedisClient = new RedisClientSingle(jedisPool);
		}
		return jedisClient;
	}
}
