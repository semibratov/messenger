package ru.semibratov.msg.console;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.semibratov.msg.AdminUser;
import ru.semibratov.msg.Message;
import ru.semibratov.msg.SimpleUser;
import ru.semibratov.msg.User;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by alex on 01.10.2016.
 */
public class UserConversationUtilImplTest {

    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
    UserConversationUtil util = new UserConversationUtilImpl();
    private User user;
    private User remoteUser;
    private Message msg1;
    private Message msg2;

    @Before
    public void before() {
        user = new SimpleUser("user1");
        remoteUser = new AdminUser("remote1");
        msg1 = createMessage(remoteUser, "Привет", true);
        msg2 = createMessage(remoteUser, "Здорово", false);
        user.addMessage(msg1);
        user.addMessage(msg2);
    }

    @Test
    public void testConverstationToString() throws Exception {
        String conversation = util.converstationToString(user, remoteUser);
        String date1 = sdf.format(msg1.getMsgTime());
        String date2 = sdf.format(msg2.getMsgTime());
        assertEquals("user1 (" + date1 + ")\n" +
                "Привет\n" +
                "*** remote1 *** (" + date2 + ")\n" +
                "Здорово\n", conversation);

    }

    private Message createMessage(User remoteUser, String text, boolean toRemote) {
        Message message = new Message();
        message.setRemoteUser(remoteUser);
        Date date1 = new Date();
        message.setMsgTime(date1);
        message.setToRemote(toRemote);
        message.setText(text);
        return message;
    }

    @Test
    public void testConverstationToFile() throws Exception {
        String fileName = "tmp.txt";
        String conversation = util.converstationToString(user, remoteUser);
        util.converstationToFile(user, remoteUser, fileName);
        String content = new String(Files.readAllBytes(Paths.get(fileName)));
        assertEquals(conversation, content);
        Files.delete(Paths.get(fileName));
    }
}