package ru.semibratov.msg;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Предок всех сервисов мессенджера. Хранит ссылки на "подключения" к Redis
 * Created by alex on 30.09.2016.
 */
public abstract class AbstractService implements InitializingBean {

    @Autowired
    JedisPool jedisPool;

    protected Jedis jedis;

    @Autowired(required = false)
    protected String keyPrefix = "";

    public void setJedis(Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.jedis = jedisPool.getResource();
    }
}
