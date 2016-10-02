package ru.semibratov.msg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Created by alex on 01.10.2016.
 */
@SpringBootApplication(scanBasePackages = "ru.semibratov.msg")
public class FakeApplication {

    public static void main(String[] args) throws InterruptedException {

        ApplicationContext ctx = SpringApplication.run(FakeApplication.class, args);

        System.exit(0);
    }
}
