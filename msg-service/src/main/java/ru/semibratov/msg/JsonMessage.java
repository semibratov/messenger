package ru.semibratov.msg;

import java.util.Date;

/**
 * Класс для преобразования сообщения в JSON
 * Created by alex on 01.10.2016.
 */
public class JsonMessage {
    private String remoteUser;
    private Date msgTime;
    private String text;
    private boolean toRemote;

    public JsonMessage() {
    }

    public JsonMessage(Message message) {
        this.remoteUser = message.getRemoteUser().getUserName();
        this.msgTime = message.getMsgTime();
        this.text = message.getText();
        this.toRemote = message.isToRemote();
    }

    public Message toMessage() {
        Message msg = new Message();
        msg.setMsgTime(msgTime);
        msg.setText(text);
        msg.setToRemote(toRemote);
        return msg;
    }

    public String getRemoteUser() {
        return remoteUser;
    }

    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }

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

    public boolean isToRemote() {
        return toRemote;
    }

    public void setToRemote(boolean toRemote) {
        this.toRemote = toRemote;
    }
}
