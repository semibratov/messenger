package ru.semibratov.msg.console;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.semibratov.msg.*;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Реализация сервиса
 * Created by alex on 01.10.2016.
 */
@Service
public class ConsoleUserInteractionImpl implements ConsoleUserInteraction, InitializingBean {

    private boolean exitFlag = false;
    private Scanner scanner = new Scanner(System.in);
    protected User selectedUser;

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLUE = "\u001B[34m";

    @Autowired
    private UserSession userSession;
    @Autowired
    UserService userService;
    @Autowired
    MessageService messageService;
    @Autowired
    UserConversationUtil conversationUtil;


    @Override
    public void run() {
        while (!exitFlag) {
            try {
                if (userSession.currentUser() == null) {
                    // войдем или создадим
                    loginOrCreate();
                } else {
                    if (selectedUser == null) {
                        selectUser();
                    } else {
                        userFunctions();
                    }
                }
            } catch (MessengerException e) {
                System.out.println(ANSI_RED + e.getMessage() + ANSI_RESET);
            }
        }

    }

    private int readNumber() {
        Integer result = null;
        while (result == null) {
            String line = scanner.nextLine();
            try {
                result = Integer.parseInt(line);
            } catch (NumberFormatException nfe) {
                printWrongInput();
            }
        }
        return result;
    }

    private void loginOrCreate() {
        System.out.println("1. Войти под существующим пользователем\n" +
                "2. Создать нового пользователя\n" +
                "0. Выход");

        int selection = readNumber();
        switch (selection) {
            case (0):
                exit();
                break;
            case (1):
                login();
                break;
            case (2):
                createUser();
                break;
            default:
                printWrongInput();
        }
    }

    private void exit() {
        exitFlag = true;
    }

    private void createUser() {
        System.out.print("Введите тип пользователя:\n" +
                "1. Обычный\n" +
                "2. Администратор");
        int typeNum = readNumber();
        UserType userType = null;
        switch (typeNum) {
            case (1):
                userType = UserType.SIMPLE;
                break;
            case (2):
                userType = UserType.ADMIN;
                break;
            default:
                printWrongInput();
        }
        System.out.println("Введите имя нового пользователя");
        String userName = scanner.nextLine();
        User user = userService.createUser(userName, userType);
        userSession.login(user.getUserName());
    }

    private void printWrongInput() {
        System.out.println(ANSI_RED + "Неверный ввод" + ANSI_RESET);
    }

    private void login() {
        System.out.println("Для входа в систему введите имя пользователя ");
        String user = scanner.nextLine();
        userSession.login(user);
    }

    private void selectUser() {
        System.out.println("Выберите пользователя");
        List<User> users = userService.loadAllUsers();
        users.remove(userSession.currentUser());
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            System.out.println( (i + 1) + ". " + user);
        }
        System.out.println("0. Выход");
        int number = readNumber();
        if (number < 0 || number > users.size()) {
            printWrongInput();
        } else {
            if (number > 0) {
                selectedUser = users.get(number - 1);
            } else {
                userSession.logout();
            }
        }
    }

    private void userFunctions() {
        User currentUser = userSession.currentUser();
        System.out.println("Вы вошли как: " + currentUser);
        System.out.println("Выбран пользователь: " + selectedUser.getDisplayName());
        System.out.println("1. Напечатать переписку с пользователем\n" +
                "2. Экспорт переписки в файл\n" +
                "3. Написать пользователю сообщение\n" +
                "0. Выход");
        int number = readNumber();
        switch (number) {
            case (0):
                selectedUser = null;
                break;
            case (1):
                String conversation = conversationUtil.converstationToString(currentUser, selectedUser);
                System.out.print(ANSI_BLUE);
                System.out.print(conversation);
                System.out.print(ANSI_RESET);
                break;
            case (2):
                System.out.println("Введите имя файла");
                String fileName = scanner.nextLine();
                try {
                    conversationUtil.converstationToFile(currentUser, selectedUser, fileName);
                    System.out.println("Переписка сохранена в файле " + fileName);
                } catch (IOException e) {
                    System.out.println("Ошибка записи");
                    System.out.println(e.getMessage());
                }
                break;
            case (3):
                System.out.println("Введите сообщение: ");
                String text = scanner.nextLine();
                messageService.sendMessage(currentUser, selectedUser, text);
                break;
            default:
                printWrongInput();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        userSession.setOnMessage(this::onMessage);
    }

    private void onMessage(Message message) {
        System.out.print(ANSI_BLUE);
        System.out.print(conversationUtil.messageToString(userSession.currentUser(), message));
        System.out.print(ANSI_RESET);
    }
}
