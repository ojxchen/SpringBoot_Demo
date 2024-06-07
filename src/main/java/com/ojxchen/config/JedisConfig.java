package com.ojxchen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class JedisConfig {

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);  // 设置最大连接数
        poolConfig.setMaxIdle(128);   // 设置最大空闲连接数
        poolConfig.setMinIdle(16);    // 设置最小空闲连接数

        // 创建 Jedis 连接池
        return new JedisPool(poolConfig, "127.0.0.1", 6379,2000);
    }
}

