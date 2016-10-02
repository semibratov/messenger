package ru.semibratov.msg;

/**
 * Сервис работы с сообщениями
 * Created by alex on 30.09.2016.
 */
public interface MessageService {
    String MSG_CHANNEL_PREFIX = "msg:";

    /**
     * Отправляет сообщение от одного пользователя к другому
     * @param from отправитель
     * @param to получатель
     * @param message текст
     */
    void sendMessage(User from, User to, String message);

    /**
     * Загрузка переписки между пользователями
     * @param user выбранный пользователь
     */
    void loadMessages(User user);

    /**
     * Преобразование строки в формате JSON в объект сообщения
     * @param json строка JSON
     * @return сообщение
     */
    Message jsonToMessage(String json);

    /**
     * Преобразование объекта в строку в формате JSON
     * @param message сообщение
     * @return строка JSON
     */
    String messageToJson(Message message);
}
