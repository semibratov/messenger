package ru.semibratov.msg.console;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import ru.semibratov.msg.MessengerException;

import java.io.*;
import java.net.URL;
import java.util.Properties;

/**
 * Created by alex on 30.09.2016.
 */
@SpringBootApplication(scanBasePackages = "ru.semibratov.msg")
public class ConsoleMessenger {

    @Bean
    JedisPool jedisPool() throws IOException {
        Properties properties = new Properties();
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("messenger.ini");
        if (resource == null) {
            throw new MessengerException("Не найден конфигурационный файл");
        }
        File file = new File(resource.getFile());
        properties.load(new FileInputStream(file));
        try {
            int port = Integer.parseInt(properties.getProperty("redisport"));
            return new JedisPool(new JedisPoolConfig(), properties.getProperty("redishost"), port);
        } catch (NumberFormatException e) {
            throw new MessengerException("Неверно указан порт Redis (" + properties.getProperty("redisport") + "");
        }
    }

    private static boolean exitFlag = false;
    public static void main(String[] args) throws InterruptedException {

        ApplicationContext ctx = SpringApplication.run(ConsoleMessenger.class, args);
        ConsoleUserInteraction userInteraction = ctx.getBean(ConsoleUserInteraction.class);
        userInteraction.run();
        System.exit(0);
    }

}
