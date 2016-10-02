package ru.semibratov.msg;

/**
 * Тип пользователя
 * Created by alex on 30.09.2016.
 */
public enum UserType {
    SIMPLE("Пользователь"), ADMIN("Администратор");

    private final String typeName;

    UserType(String typeName) {
        this.typeName = typeName;
    }
}
