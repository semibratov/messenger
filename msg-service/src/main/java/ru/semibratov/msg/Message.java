package ru.semibratov.msg;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

/**
 * Сообщение меджу пользователями
 * Created by alex on 30.09.2016.
 */
public class Message {

    /**
     * Пользователь на "той стороне"
     */
    @JsonIgnore
    private User remoteUser;

    /**
     * Время сообщения
     */
    private Date msgTime;
    /**
     * Текст сообщения
     */
    private String text;
    /**
     * "Направление" сообщения. false - удаленному пользователю, true - от удаленного пользователя
     */
    private boolean toRemote;

    public Date getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(Date msgTime) {
        this.msgTime = msgTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getRemoteUser() {
        return remoteUser;
    }

    public void setRemoteUser(User remoteUser) {
        this.remoteUser = remoteUser;
    }

    public boolean isToRemote() {
        return toRemote;
    }

    public void setToRemote(boolean toRemote) {
        this.toRemote = toRemote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (toRemote != message.toRemote) return false;
        if (!remoteUser.equals(message.remoteUser)) return false;
        if (!msgTime.equals(message.msgTime)) return false;
        return !(text != null ? !text.equals(message.text) : message.text != null);

    }

    @Override
    public int hashCode() {
        int result = remoteUser.hashCode();
        result = 31 * result + msgTime.hashCode();
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (toRemote ? 1 : 0);
        return result;
    }
}
