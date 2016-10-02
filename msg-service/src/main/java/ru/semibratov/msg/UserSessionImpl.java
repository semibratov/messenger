package ru.semibratov.msg;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by alex on 01.10.2016.
 */
@Service
public class UserSessionImpl extends AbstractService implements UserSession, InitializingBean {

    MessageListener listener;

    /**
     * Свой экземпляр для работы в другом потоке
     */
    private Jedis threadJedis;

    @Autowired
    protected UserService userService;
    @Autowired
    protected MessageService messageService;

    @Autowired
    private ApplicationContext appCtx;
    private User user;
    private Consumer<Message> onMessage;

    @Override
    public User login(String userName) {
        if (user != null) {
            throw new MessengerException("Предыдущий пользователь не завершил сессию");
        }
        user = userService.findUser(userName);
        if (user == null) {
            throw new MessengerException("Пользователь с таким именем не найден");
        }
        messageService.loadMessages(user);
        listener = appCtx.getBean(MessageListener.class);
        listener.setOnMessage(onMessage);
        listener.setUser(user);
        runListenerThread(user, listener);
        return user;
    }

    @Override
    public User currentUser() {
        return user;
    }

    @Override
    public void logout() {
        if (user != null) {
            user = null;
            listener.unsubscribe();
            listener = null;
        }
    }

    @Override
    public void setOnMessage(Consumer<Message> lambda) {
        this.onMessage = lambda;
        if (listener != null) {
            listener.setOnMessage(onMessage);
        }
    }

    private void runListenerThread(User user, MessageListener listener) {
        CountDownLatch latch = new CountDownLatch(1);
        listener.setLatch(latch);
        Runnable runnable = () -> threadJedis.subscribe(listener, MessageService.MSG_CHANNEL_PREFIX + user.getUserName());
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            // Ждем пока не "включится" подписка, а то тесты падают, так как проверяют до того, как выполнится subscribe
            if (!latch.await(50, TimeUnit.MILLISECONDS)) {
                throw new MessengerException("Ошибка создания подписки на сообщения");
            }
        } catch (InterruptedException ignored) {
            // Сигнал на выход - игнорим
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        this.threadJedis = jedisPool.getResource();
    }

}
