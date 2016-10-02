package ru.semibratov.msg;

import java.util.*;

/**
 * Обычный пользователь
 * Created by alex on 30.09.2016.
 */
public class SimpleUser implements User {
    String userName;
    private Map<User, List<Message>> messages = new HashMap<>();

    public SimpleUser(String userName) {
        this.userName = userName;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getDisplayName() {
        return getUserName();
    }

    @Override
    public List<Message> getMessages(User user) {
        List<Message> list = messages.get(user);
        return list == null ? Collections.EMPTY_LIST : Collections.unmodifiableList(list);
    }

    @Override
    public UserType getUserType() {
        return UserType.SIMPLE;
    }

    @Override
    public synchronized void addMessage(Message msg) {
        User user = msg.getRemoteUser();
        if (user == null) {
            throw new MessengerException("Пустой абонент в сообщении");
        }
        if (user.equals(this)) {
            throw new MessengerException("Нельзя отправлять сообщение самому себе");
        }
        List<Message> list = messages.get(user);
        if (list == null) {
            list = new ArrayList<>();
            messages.put(user, list);
        }
        list.add(msg);
        messages.put(user, list);
    }

    @Override
    public void clearMessages() {
        messages.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleUser that = (SimpleUser) o;

        return userName.equals(that.userName);
    }

    @Override
    public int hashCode() {
        return userName.hashCode();
    }

    @Override
    public int compareTo(User o) {
        return this.getUserName().compareTo(o.getUserName());
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
