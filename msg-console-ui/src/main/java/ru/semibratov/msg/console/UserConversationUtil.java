package ru.semibratov.msg.console;

import ru.semibratov.msg.Message;
import ru.semibratov.msg.User;

import java.io.IOException;

/**
 * Утилитные функции для работы с сообщениями
 * Created by alex on 01.10.2016.
 */
public interface UserConversationUtil {

    String converstationToString(User user, User remoteUser);

    String messageToString(User user, Message msg);

    void converstationToFile(User user, User remoteUser, String fileName) throws IOException;
}
