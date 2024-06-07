package com.ojxchen;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static org.junit.Assert.assertEquals;

@SpringBootTest
public class JedisTest {
    @Autowired
    private JedisPool jedisPool;

    // 设置值
    @Test
    public void setValue() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("a", "1");
            jedis.pexpire("a", 10000);
        }
    }

    // 获取值
    @Test
    public void getValue() {
        try (Jedis jedis = jedisPool.getResource()) {
            assertEquals("1",jedis.get("a"));
        }
    }

    // 过期测试
    @Test
    public void getOverdueValue() {
        try (Jedis jedis = jedisPool.getResource()) {
            assertEquals(null,jedis.get("a"));
        }
    }


}

