package ru.semibratov.msg.console;

import org.springframework.stereotype.Service;
import ru.semibratov.msg.Message;
import ru.semibratov.msg.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

/**
 * Реализация сервиса
 * Created by alex on 01.10.2016.
 */
@Service
public class UserConversationUtilImpl implements UserConversationUtil {
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

    @Override
    public String converstationToString(User user, User remoteUser) {
        String text = "";
        for (Message msg: user.getMessages(remoteUser)) {
            text += messageToString(user, msg);
        }
        return text;
    }

    @Override
    public String messageToString(User user, Message msg) {
        String text;
        if (msg.isToRemote()) {
            text = user.getDisplayName();
        } else {
            text = msg.getRemoteUser().getDisplayName();
        }
        String dateStr = sdf.format(msg.getMsgTime());
        text = text + " (" + dateStr + ")\n" + msg.getText() + "\n";
        return text;
    }

    @Override
    public void converstationToFile(User user, User remoteUser, String fileName) throws IOException {
        String conversation = converstationToString(user, remoteUser);
        Files.write(Paths.get(fileName), conversation.getBytes());
    }
}
