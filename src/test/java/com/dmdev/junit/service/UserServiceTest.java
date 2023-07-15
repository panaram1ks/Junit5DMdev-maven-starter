package com.dmdev.junit.service;

import com.dmdev.junit.dto.User;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

//@TestInstance(TestInstance.Lifecycle.PER_METHOD) // default lifecycle!
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {

    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "111");
    private UserService userService;

    @BeforeAll
//    static void init() {
    void init() {
        System.out.println("Before All: ");
    }

    @BeforeEach
    void prepare() {
        System.out.println("Before each: " + this.toString());
        userService = new UserService();
    }

    @Test
    void usersEmptyIfNoUserAdded() {
        System.out.println("Test 1: " + this.toString());
        List<User> users = userService.getAll();
        assertTrue(users.isEmpty(), () -> "User list should be empty"); //  input ->  [box == function] -> actual output
    }

    @Test
    void usersSizeIfUserAdded() {
        System.out.println("Test 2: " + this.toString());
        userService.add(IVAN);
        userService.add(PETR);

        List<User> users = userService.getAll();
        assertEquals(2, users.size());
    }

    @Test
    void loginSuccessIfUserExist() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());
        assertTrue(maybeUser.isPresent());
        maybeUser.ifPresent(user -> assertEquals(IVAN, user));

    }

    @Test
    void loginFailIfPasswordIsNotCorrect() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login(IVAN.getUsername(), "dummy");
        assertTrue(maybeUser.isEmpty());
    }

    @Test
    void loginFailIfUserDoesNotExist() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login("dummy", "anyPassword");
        assertTrue(maybeUser.isEmpty());
    }

    @AfterEach
    void deleteDataFromDatabase() {
        System.out.println("After each: " + this.toString());
    }

    @AfterAll
//    static void closeConnectionPool() {
    void closeConnectionPool() {
        System.out.println("After All: ");
    }
}
