package ru.semibratov.msg;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Реализация сервиса
 * Created by alex on 01.10.2016.
 */
public class UserSessionImplTest extends AbstractJedisTest {

    private User user1;
    private User user2;

    @Before
    @Override
    public void before() {
        super.before();
        user1 = userService.createUser("login1", UserType.ADMIN);
        user2 = userService.createUser("login2", UserType.SIMPLE);
        messageService.sendMessage(user1, user2, "Привет");
        messageService.sendMessage(user2, user1, "Хай");
        userSession.logout();
    }

    @After
    @Override
    public void after() throws InterruptedException {
        super.after();
        userSession.logout();
    }

    @Test
    public void testLogin() throws Exception {
        // проверим что пользователь и его сообщения загружаются при логине
        User logged = userSession.login("login1");
        assertEquals(2, logged.getMessages(user2).size());
        assertEquals(logged, userSession.currentUser());
    }

    @Test
    public void testLogout() throws Exception {
        // проверим что пользователь и его сообщения загружаются при логине
        User logged = userSession.login("login1");
        assertEquals(logged, userSession.currentUser());
        userSession.logout();
        assertNull(userSession.currentUser());
    }

    @Test
    public void testAutoReceiveMessage() throws InterruptedException {
        User logged = userSession.login("login2");
        int msgCount = logged.getMessages(user1).size();
        messageService.sendMessage(user1, user2, "Как дела?");
        // залогированный пользователь должен автоматически принимать сообщения, отправленные ему из других клиентов
        // ждем выполнения в другом потоке
        Thread.sleep(100);
        assertEquals(msgCount + 1, logged.getMessages(user1).size());
    }

    @Test(expected = MessengerException.class)
    public void testDuplicateLoginError() throws InterruptedException {
        User logged1 = userSession.login("login1");
        User logged2 = userSession.login("login2");
    }

    @Test
    public void testNotReceiveMessageAfterLogout() throws InterruptedException {
        User logged1 = userSession.login("login2");
        User user3 = userService.createUser("login3", UserType.SIMPLE);
        userSession.logout();
        assertEquals(2, logged1.getMessages(user1).size());
        messageService.sendMessage(user1, user2, "Как дела?");
        // После того как второй пользователь залогинился, первый не должен получать сообщения
        assertEquals(2, logged1.getMessages(user1).size());
    }

    @Test(expected = MessengerException.class)
    public void testWrongLogin() {
        userSession.login("not_exists");
    }


}