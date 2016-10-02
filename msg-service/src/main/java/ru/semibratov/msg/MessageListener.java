package ru.semibratov.msg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * Слушатель сообщений залогиненного пользователя
 * Created by alex on 01.10.2016.
 */
@Component
@Scope("prototype")
public class MessageListener extends JedisPubSub {

    @Autowired
    protected MessageService messageService;
    @Autowired
    UserService userService;

    CountDownLatch latch;
    private Consumer<Message> onMessage;

    private User user;

    @Override
    public void onMessage(String channel, String jsonMessage) {
        Message msg = messageService.jsonToMessage(jsonMessage);
        user.addMessage(msg);
        if (onMessage != null) {
            onMessage.accept(msg);
        }
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        // Чтобы родительский поток знал, что подписка состоялась
        if (latch != null) {
            latch.countDown();
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    public void setOnMessage(Consumer<Message> onMessage) {
        this.onMessage = onMessage;
    }
}
