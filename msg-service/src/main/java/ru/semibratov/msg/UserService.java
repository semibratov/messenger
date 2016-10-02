package ru.semibratov.msg;

import java.util.List;

/**
 * Сервис работы с пользователями
 * Created by alex on 30.09.2016.
 */
public interface UserService {

    /**
     * Создает и сохраняет пользователя
     * @param userName имя пользователя
     * @param userType тип пользователя
     * @return пользователь
     */
    User createUser(String userName, UserType userType);

    /**
     * Загружает ранее созданного пользователя по имени
     * @param userName имя
     * @return пользователь
     */
    User findUser(String userName);

    /**
     * Загрузка всех пользователей
     * @return список пользователей
     */
    List<User> loadAllUsers();
}
