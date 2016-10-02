package ru.semibratov.msg;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

/**
 * Created by alex on 30.09.2016.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.NONE, classes = TestConfiguration.class)
public abstract class AbstractJedisTest {


    @Autowired
    protected MessageService messageService;
    @Autowired
    protected UserService userService;
    @Autowired
    protected UserSession userSession;
    @Autowired
    protected Jedis jedis;

    @Before
    public void before() {
        // очищаем записи до теста (могли остаться, если тесты завершены с ошибками)
        clearRecords();
    }
    @After
    public void after() throws InterruptedException {
        // очищаем записи после теста
        clearRecords();
    }

    public void clearRecords() {
        for (String key: jedis.keys(TestConfiguration.PREFIX + "*")) {
            jedis.del(key);
        }
    }

}
