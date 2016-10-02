package ru.semibratov.msg;

import java.util.function.Consumer;

/**
 * Сессия для работы пользователя
 * Created by alex on 01.10.2016.
 */
public interface UserSession {

    /**
     * "Авторизация" пользователя
     * @param userName имя
     * @return пользователь
     */
    User login(String userName);

    /**
     * Текущий авторизаванный пользователь
     * @return текущий пользователь или null, если вход не выполнен
     */
    User currentUser();

    /**
     * Завершение сессии текущего пользователя
     */
    void logout();

    /**
     * Установка дополнительного обработчика при получении сообщения
     * @param lambda ссылка на функцию
     */
    void setOnMessage(Consumer<Message> lambda);
}
