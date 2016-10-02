package ru.semibratov.msg;

import org.junit.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by alex on 30.09.2016.
 */
public class UserServiceImplTest extends AbstractJedisTest {

    @Before
    public void before() {
        // хак - обнуления кэша
        ((UserServiceImpl)userService).clearCache();
    }

    @Test
    public void testCreateSimpleUser() throws Exception {
        User user1 = userService.createUser("login", UserType.SIMPLE);
        assertTrue(user1 instanceof SimpleUser);
        assertEquals("login", user1.getUserName());
        assertEquals("login", user1.getDisplayName());
    }

    @Test
    public void testCreateAdminUser() throws Exception {
        User user1 = userService.createUser("login", UserType.ADMIN);
        assertTrue(user1 instanceof AdminUser);
        assertEquals("login", user1.getUserName());
        assertEquals("*** login ***", user1.getDisplayName());
    }

    @Test(expected = MessengerException.class)
    public void testCreateUserDupName() throws Exception {
        // Нелья создать пользователей с одинаковыми именами
        userService.createUser("login", UserType.SIMPLE);
        userService.createUser("login", UserType.SIMPLE);
    }

    @Test
    public void testLoadUser() {
        User user1 = userService.createUser("login", UserType.SIMPLE);
        User user2 = userService.findUser("login");
        assertEquals(user1, user2);
    }

    @Test
    public void testLoadAdminUser() {
        User admin1 = userService.createUser("login", UserType.ADMIN);
        User admin2 = userService.findUser("login");
        assertEquals(admin1, admin2);
    }

    @Test(expected = MessengerException.class)
    public void testCreateUserWithColon() {
        userService.createUser("log:in", UserType.ADMIN);
    }

    @Test(expected = MessengerException.class)
    public void testCreateUserWithAster() {
        userService.createUser("log*in", UserType.ADMIN);
    }

    @Test
    public void testLoadAllUsers() {
        assertTrue(userService.loadAllUsers().isEmpty());
        User user1 = userService.createUser("user1", UserType.SIMPLE);
        User user2 = userService.createUser("user2", UserType.SIMPLE);
        User admin1 = userService.createUser("admin1", UserType.ADMIN);
        List<User> users = userService.loadAllUsers();
        // Должны быть отсортированы по имени
        Collections.sort(users);
        assertEquals(Arrays.asList(admin1, user1, user2), users);
    }

}