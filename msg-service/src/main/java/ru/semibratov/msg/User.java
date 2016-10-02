package ru.semibratov.msg;

import java.util.List;

/**
 * Пользователь мессенджера
 * Created by alex on 30.09.2016.
 */
public interface User extends Comparable<User> {
    String getUserName();
    String getDisplayName();
    List<Message> getMessages(User user);
    UserType getUserType();

    void addMessage(Message msg);
    void clearMessages();
}
