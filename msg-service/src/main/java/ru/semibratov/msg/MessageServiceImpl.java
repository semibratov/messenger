package ru.semibratov.msg;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;

/**
 * Реализация сервиса
 * Created by alex on 30.09.2016.
 */
@Service
public class MessageServiceImpl extends AbstractService implements MessageService {

    @Autowired
    private UserService userService;

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void sendMessage(User from, User to, String text) {
        // Сохраняем два сообщения - для отправителя и получателя отдельно (для быстрого поиска)
        Message msg1 = new Message();
        msg1.setText(text);
        msg1.setRemoteUser(to);
        msg1.setMsgTime(new Date());
        msg1.setToRemote(true);
        from.addMessage(msg1);
        saveMessage(from, msg1);

        Message msg2 = new Message();
        msg2.setText(text);
        msg2.setRemoteUser(from);
        msg2.setMsgTime(new Date());
        msg2.setToRemote(false);
        to.addMessage(msg2);
        saveMessage(to, msg2);
        publishMessage(to, msg2);
    }

    private void publishMessage(User to, Message msg) {
        String json = messageToJson(msg);
        jedis.publish(MSG_CHANNEL_PREFIX + to.getUserName(), json);
    }

    private Message saveMessage(User user, Message message) {
        String key = messageKey(user.getUserName());
        String json = messageToJson(message);
        jedis.rpush(key, json);
        return message;
    }

    private String messageKey(String userName) {
        return keyPrefix + "msg:" + userName;
    }

    @Override
    public void loadMessages(User user) {
        String key = messageKey(user.getUserName());
        List<String> jsonList = jedis.lrange(key, 0, -1);
        user.clearMessages();
        for (String json: jsonList) {
            Message msg;
            msg = jsonToMessage(json);
            user.addMessage(msg);
        }
    }

    @Override
    public Message jsonToMessage(String json) {
        Message msg;
        StringReader reader = new StringReader(json);
        try {
            JsonMessage jsonMsg = mapper.readValue(reader, JsonMessage.class);
            msg = jsonMsg.toMessage();
            User user = userService.findUser(jsonMsg.getRemoteUser());
            msg.setRemoteUser(user);
        } catch (IOException ignored) {
            throw new MessengerException("Ошибка преобразования сообщения в JSON");
        }
        return msg;
    }

    @Override
    public String messageToJson(Message message) {
        String json;
        Writer w = new StringWriter();
        try {
            JsonMessage jsonMessage = new JsonMessage(message);
            mapper.writeValue(w, jsonMessage);
            json = w.toString();
        } catch (IOException ignored) {
            throw new MessengerException("Ошибка преобразования сообщения в JSON");
        }
        return json;
    }

}
