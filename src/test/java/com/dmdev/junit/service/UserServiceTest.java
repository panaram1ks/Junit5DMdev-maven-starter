package com.dmdev.junit.service;

import com.dmdev.junit.dto.User;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.*;


//@TestInstance(TestInstance.Lifecycle.PER_METHOD) // default lifecycle!
@Tag("fast")
@Tag("user")
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

        MatcherAssert.assertThat(users, empty());
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
    @Tag("login")
    void loginSuccessIfUserExist() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());
//        assertTrue(maybeUser.isPresent());
        assertThat(maybeUser).isPresent();
//        maybeUser.ifPresent(user -> assertEquals(IVAN, user));
        maybeUser.ifPresent(user -> assertThat(IVAN).isEqualTo(user));
    }

    @Test
    @Tag("login")
    void throwExceptionIfUsernameOrPasswordIsNull() {
//        try {
//            userService.login(null, "dummy");
//            fail("login should throw exception on null username");
//        } catch (IllegalArgumentException ex) {
//            assertTrue(true);
//        }
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> userService.login(null, "dummy")),
                () -> assertThrows(IllegalArgumentException.class, () -> userService.login("dummy", null))
        );

    }

    @Test
    void userConvertedToMapById() {
        userService.add(IVAN, PETR);
        Map<Integer, User> users = userService.getAllConvertedById();
        assertAll( // will check all asserts even if first failed
                () -> assertThat(users).containsKeys(IVAN.getId(), PETR.getId()),
                () -> assertThat(users).containsValues(IVAN, PETR)
        );
        MatcherAssert.assertThat(users, IsMapContaining.hasKey(IVAN.getId()));
    }

    @Test
    @Tag("login")
    void loginFailIfPasswordIsNotCorrect() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login(IVAN.getUsername(), "dummy");
//        assertTrue(maybeUser.isEmpty());
        assertThat(maybeUser).isEmpty();
    }

    @Test
    @Tag("login")
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

// mvn clean test -Dgroups=login
// mvn clean test -Dgroups=fast
