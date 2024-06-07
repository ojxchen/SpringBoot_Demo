package com.ojxchen;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private JedisPool jedisPool;

    @Test
    public void setValue() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("a", "1");
        }
    }

    @Test
    public void getValue() {
        try (Jedis jedis = jedisPool.getResource()) {
            String s = jedis.get("a");
            System.out.println(s);
        }
    }

}
