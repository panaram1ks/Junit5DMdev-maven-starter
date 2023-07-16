package com.dmdev.junit.service;

import com.dmdev.junit.dao.UserDao;
import com.dmdev.junit.dto.User;
import com.dmdev.junit.extention.*;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.RepeatedTest.LONG_DISPLAY_NAME;


//@TestInstance(TestInstance.Lifecycle.PER_METHOD) // default lifecycle!
@Tag("fast")
@Tag("user")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ExtendWith({
        UserServiceParamResolver.class,
//        GlobalExtension.class,
        PostProcessingExtension.class,
        ConditionalExtension.class,
//        ThrowableException.class
})
@Timeout(value = 200, unit = TimeUnit.MILLISECONDS)
public class UserServiceTest {

    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "111");

    private UserDao userDao;
    private UserService userService;

    UserServiceTest(TestInfo testInfo) {
        System.out.println();
    }

    @BeforeAll
//    static void init() {
    void init() {
        System.out.println("Before All: ");
    }

    @BeforeEach
    void prepare() {
        System.out.println("Before each: " + this.toString());
        this.userDao = Mockito.mock(UserDao.class);
        this.userService = new UserService(userDao);
    }

    @Test
    void shouldDeleteExistedUser() {
        userService.add(IVAN);
//        Mockito.doReturn(true).when(userDao).delete(IVAN.getId());

        Mockito.when(userDao.delete(IVAN.getId()))
                .thenReturn(true)
                .thenReturn(false);
        boolean deleteResult = userService.delete(IVAN.getId());
        System.out.println(deleteResult);
        System.out.println(userService.delete(IVAN.getId()));
        System.out.println(userService.delete(IVAN.getId()));

//        Mockito.doReturn(true).when(userDao).delete(Mockito.any());
//        boolean delete = userService.delete(IVAN.getId());
//        boolean delete2 = userService.delete(2);
//        assertThat(delete).isTrue();
//        assertThat(delete2).isTrue();
    }

    @Test
    @Order(1)
    @DisplayName("Users will be empty if no users added")
    void usersEmptyIfNoUserAdded() throws IOException {
        if (true) {
            throw new RuntimeException();
        }
        System.out.println("Test 1: " + this.toString());
        List<User> users = userService.getAll();
//        assertThat(users).hasSize(2);
//        assertTrue(users.isEmpty(), () -> "User list should be empty"); //  input ->  [box == function] -> actual output

        MatcherAssert.assertThat(users, empty());
    }

    @Test
    @Order(2)
    void usersSizeIfUserAdded() {
        System.out.println("Test 2: " + this.toString());
        userService.add(IVAN);
        userService.add(PETR);

        List<User> users = userService.getAll();
//        assertEquals(2, users.size());
        assertThat(users).hasSize(2);
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

    @AfterEach
    void deleteDataFromDatabase() {
        System.out.println("After each: " + this.toString());
    }

    @AfterAll
//    static void closeConnectionPool() {
    void closeConnectionPool() {
        System.out.println("After All: ");
    }

    @Nested
    @DisplayName("test user login functionality")
    @Tag("login")
    class LoginTest {
        @Test
        void loginSuccessIfUserExist() {
            userService.add(IVAN);
            Optional<User> maybeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());
            assertThat(maybeUser).isPresent();
            maybeUser.ifPresent(user -> assertThat(IVAN).isEqualTo(user));
        }

        @Test
        void throwExceptionIfUsernameOrPasswordIsNull() {
            assertAll(
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login(null, "dummy")),
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login("dummy", null))
            );
        }

        @Test
        @RepeatedTest(value = 5, name = LONG_DISPLAY_NAME)
        void loginFailIfPasswordIsNotCorrect() {

            userService.add(IVAN);
            Optional<User> maybeUser = userService.login(IVAN.getUsername(), "dummy");
            assertThat(maybeUser).isEmpty();
        }

        @Test
//        @Timeout(value = 200, unit = TimeUnit.MILLISECONDS)
        void checkLoginFunctionalityPerformance() {
            System.out.println(Thread.currentThread().getName());
            Optional<User> maybeUser = assertTimeoutPreemptively(Duration.ofMillis(200), () -> {
                Thread.sleep(10);
                System.out.println(Thread.currentThread().getName());
                return userService.login(IVAN.getUsername(), "dummy");
            });
        }

        @Test
        @Disabled("flaky, need to see")
        void loginFailIfUserDoesNotExist() {
            userService.add(IVAN);
            Optional<User> maybeUser = userService.login("dummy", "anyPassword");
            assertThat(maybeUser).isEmpty();
        }

        @ParameterizedTest(name = "{arguments} test")
//        @ArgumentsSource()
//        @NullSource
//        @EmptySource
//        @NullAndEmptySource
//        @ValueSource(strings = {"Ivan", "Petr"})
//        @EmptySource
        @MethodSource("com.dmdev.junit.service.UserServiceTest#getArgumentsForLoginTest")
//        @CsvFileSource(resources = "/login-test-data.csv", delimiter = ',', numLinesToSkip = 1)
//        @CsvSource({
//                "Ivan,123",
//                "Petr,111"
//        })
        void loginParametrizedTest(String username, String password, Optional<User> user) {
            userService.add(IVAN, PETR);
            Optional<User> maybeUser = userService.login(username, password);
            assertThat(maybeUser).isEqualTo(user);
        }

    }

    static Stream<Arguments> getArgumentsForLoginTest() {
        return Stream.of(
                Arguments.of("Ivan", "123", Optional.of(IVAN)),
                Arguments.of("Petr", "111", Optional.of(PETR)),
                Arguments.of("Petr", "dummy", Optional.empty()),
                Arguments.of("dummy", "111", Optional.empty())
        );
    }
}

// mvn clean test -Dgroups=login
// mvn clean test -Dgroups=fast
