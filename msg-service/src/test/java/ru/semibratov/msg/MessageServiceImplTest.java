package ru.semibratov.msg;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

import static org.junit.Assert.*;

/**
 * Created by alex on 30.09.2016.
 */
public class MessageServiceImplTest extends AbstractJedisTest {

    @Test
    public void testSendMessage() throws Exception {
        User from = userService.createUser("sender", UserType.SIMPLE);
        User to = userService.createUser("receiver", UserType.SIMPLE);
        messageService.sendMessage(from, to, "сообщение");
        // Отправленное сообщение
        assertEquals(1, from.getMessages(to).size());
        Message msg1 = from.getMessages(to).get(0);
        assertNotNull(msg1.getMsgTime());
        assertEquals("сообщение", msg1.getText());
        assertEquals(to, msg1.getRemoteUser());
        assertTrue(msg1.isToRemote());

        // Полученное сообщение
        assertEquals(1, to.getMessages(from).size());
        Message msg2 = to.getMessages(from).get(0);
        assertNotNull(msg2.getMsgTime());
        assertEquals("сообщение", msg2.getText());
        assertEquals(from, msg2.getRemoteUser());
        assertFalse(msg2.isToRemote());

        // проверим что сообщения сохранились
        from = userService.findUser("sender");
        messageService.loadMessages(from);
        assertEquals(1, from.getMessages(to).size());
        assertEquals(msg1, from.getMessages(to).get(0));

        to = userService.findUser("receiver");
        messageService.loadMessages(to);
        assertEquals(1, to.getMessages(from).size());
        assertEquals(msg2, to.getMessages(from).get(0));
    }

    @Test(expected = MessengerException.class)
    public void testSendMessageEqualUsers() {
        User user = userService.createUser("sender", UserType.SIMPLE);
        messageService.sendMessage(user, user, "сообщение");
    }

    @Test
    public void loadMessagesWithExisingMessages() {
        User from = userService.createUser("sender", UserType.SIMPLE);
        User to = userService.createUser("receiver", UserType.SIMPLE);
        messageService.sendMessage(from, to, "сообщение");
        assertEquals(1, from.getMessages(to).size());
        messageService.loadMessages(from);
        assertEquals(1, from.getMessages(to).size());
    }

    @Test
    public void sendMessageCallFunction() throws InterruptedException {
        User from = userService.createUser("sender", UserType.SIMPLE);
        userService.createUser("receiver", UserType.SIMPLE);
        User to = userSession.login("receiver");
        Message[] messages = new Message[1];
        Consumer<Message> lambda = message -> messages[0] = message;
        userSession.setOnMessage(lambda);
        messageService.sendMessage(from, to, "сообщение");
        Thread.sleep(100); // ждем пока придет сообщение
        assertEquals("сообщение", messages[0].getText());
        assertEquals(from, messages[0].getRemoteUser());
        assertEquals(false, messages[0].isToRemote());
    }
}