package com.dmdev.junit.service;

import com.dmdev.junit.dto.User;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


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
        assertThat(users).hasSize(2);
//        assertTrue(users.isEmpty(), () -> "User list should be empty"); //  input ->  [box == function] -> actual output
    }

    @Test
    void usersSizeIfUserAdded() {
        System.out.println("Test 2: " + this.toString());
        userService.add(IVAN);
        userService.add(PETR);

        List<User> users = userService.getAll();
//        assertEquals(2, users.size());
        assertThat(users).hasSize(2);
    }

    @Test
    void loginSuccessIfUserExist() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());
//        assertTrue(maybeUser.isPresent());
        assertThat(maybeUser).isPresent();
//        maybeUser.ifPresent(user -> assertEquals(IVAN, user));
        maybeUser.ifPresent(user -> assertThat(IVAN).isEqualTo(user));
    }

    @Test
    void userConvertedToMapById() {
        userService.add(IVAN, PETR);
        Map<Integer, User> users = userService.getAllConvertedById();
        assertAll( // will check all asserts even if first failed
                () -> assertThat(users).containsKeys(IVAN.getId(), PETR.getId()),
                () -> assertThat(users).containsValues(IVAN, PETR)
        );
    }

    @Test
    void loginFailIfPasswordIsNotCorrect() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login(IVAN.getUsername(), "dummy");
//        assertTrue(maybeUser.isEmpty());
        assertThat(maybeUser).isEmpty();
    }

    @Test
    void loginFailIfUserDoesNotExist() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login("dummy", "anyPassword");
//        assertTrue(maybeUser.isEmpty());
        assertThat(maybeUser).isEmpty();
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
