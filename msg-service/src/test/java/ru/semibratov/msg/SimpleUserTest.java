package ru.semibratov.msg;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by alex on 30.09.2016.
 */
public class SimpleUserTest {

    User user1;
    User user2;
    User user3;

    @Before
    public void before() {
        user1 = new SimpleUser("login1");
        user2 = new SimpleUser("login2");
        user3 = new SimpleUser("login3");
    }

    private Message createMessage(User user2, String text, boolean toRemote) {
        Message msg = new Message();
        msg.setText(text);
        msg.setRemoteUser(user2);
        msg.setToRemote(toRemote);
        return msg;
    }

    @Test
    public void testGetMessagesFromUser() throws Exception {
        Message msg1 = createMessage(user2, "Привет", true);
        Message msg2 = createMessage(user3, "Хай", true);
        user1.addMessage(msg1);
        user1.addMessage(msg2);
        assertEquals(Arrays.asList(msg1), user1.getMessages(user2));
        assertEquals(Arrays.asList(msg2), user1.getMessages(user3));
        assertTrue(user1.getMessages(new SimpleUser("login10")).isEmpty());
    }

    @Test(expected = MessengerException.class)
    public void testAddWrongMessage() {
        Message msg1 = createMessage(user1, "Привет", false);
        user1.addMessage(msg1);

    }
}