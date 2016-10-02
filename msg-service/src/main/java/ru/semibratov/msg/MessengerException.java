package ru.semibratov.msg;

import org.omg.SendingContext.RunTime;

/**
 * Исключение мессенджера
 * Created by alex on 30.09.2016.
 */
public class MessengerException extends RuntimeException {

    public MessengerException(String msg) {
        super(msg);
    }
}
