package ru.semibratov.msg;

/**
 * Пользователь-администратор
 * Created by alex on 30.09.2016.
 */
public class AdminUser extends SimpleUser {

    public AdminUser(String userName) {
        super(userName);
    }

    @Override
    public String getDisplayName() {
        return "*** " + getUserName() + " ***";
    }

    @Override
    public UserType getUserType() {
        return UserType.ADMIN;
    }
}
