package ru.semibratov.msg;

import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Реализация сервиса
 * Created by alex on 30.09.2016.
 */
@Service
public class UserServiceImpl extends AbstractService implements UserService {

    /**
     * Кэш пользователей. Так как пользователи не могут меняться - храниться без ограничения
     */
    Map<String, User> userCache = new HashMap<>();

    public synchronized void clearCache() {
        userCache.clear();
    }

    @Override
    public User createUser(String userName, UserType userType) {
        if (userName.contains(":")) {
            throw new MessengerException("Нелья использовать ':' (двоеточие) в имени пользователя");
        }
        if (userName.contains("*")) {
            throw new MessengerException("Нелья использовать '*' (звездочку) в имени пользователя");
        }
        User user = instantiateUser(userName, userType);
        addUserToRedis(user);
        return user;
    }

    private User instantiateUser(String userName, UserType userType) {
        User user = null;
        switch (userType) {
            case SIMPLE:
                user = new SimpleUser(userName);
                break;
            case ADMIN:
                user = new AdminUser(userName);
                break;
        }
        return user;
    }

    private User addUserToRedis(User user) {
        String key = userKey(user.getUserName());
        if (jedis.exists(key)) {
            throw new MessengerException("Пользователь с именем " + user.getUserName() + " уже существует");
        }
        jedis.set (key, user.getUserType().toString());
        // Добавляем пользователя в множество пользователей, чтобы не перебирать ключи при загрузке всех пользователей
        jedis.sadd(allUsersKey(), user.getUserName());
        return user;
    }

    private String userKey(String user) {
        return keyPrefix + "user:" + user;
    }
    private String allUsersKey() {
        return keyPrefix + "allusers";
    }

    @Override
    public synchronized User findUser(String userName) {
        User user = userCache.get(userName);
        if (user != null) {
            return user;
        }
        user = loadUserFormRedis(userName);
        if (user != null) {
            userCache.put(userName, user);
        }
        return user;
    }

    private User loadUserFormRedis(String userName) {
        String key = userKey(userName);
        String type = jedis.get(key);
        if (type == null) {
            return null; // пользователь не найден
        }
        UserType userType = UserType.valueOf(type);
        return instantiateUser(userName, userType);
    }

    @Override
    public List<User> loadAllUsers() {
        List<User> list = new ArrayList<>();
        // Список имен пользователей хранится отдельно
        Set<String> allNames = jedis.smembers(allUsersKey());
        for (String userName : allNames) {
            User user = findUser(userName);
            list.add(user);
        }
        return list;
    }

}
