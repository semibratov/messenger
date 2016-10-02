package ru.semibratov.msg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Spring конфигурация для тестов. Для работы тестов должен быть установлен redis на localhost
 * Created by alex on 01.10.2016.
 */
@Configuration
@ComponentScan(basePackages = "ru.semibratov.msg")
public class TestConfiguration {

    protected static final String PREFIX = "tst:";
    private JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost");

    @Bean
    JedisPool jedisPool() {
        return jedisPool;
    }

    @Bean
    Jedis jedis() {
        return jedisPool.getResource();
    }

    @Bean
    String keyPrefix() {
        return PREFIX;
    }

}
