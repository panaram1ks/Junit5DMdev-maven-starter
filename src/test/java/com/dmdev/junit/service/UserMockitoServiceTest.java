package com.dmdev.junit.service;


import com.dmdev.junit.dao.UserDao;
import com.dmdev.junit.dto.User;
import com.dmdev.junit.extention.ConditionalExtension;
import com.dmdev.junit.extention.PostProcessingExtension;
import com.dmdev.junit.extention.UserServiceParamResolver;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.withSettings;

@Tag("fast")
@Tag("user")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ExtendWith({
        UserServiceParamResolver.class,
        PostProcessingExtension.class,
        ConditionalExtension.class,
        MockitoExtension.class
})
@Timeout(value = 200, unit = TimeUnit.MILLISECONDS)
public class UserMockitoServiceTest {

    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "111");

    @Captor
    private ArgumentCaptor<Integer> argumentCaptor;
// 1 @Mock(lenient = true)
    @Mock(lenient = true)
    private UserDao userDao;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void prepare() {
// 3        lenient().when(userDao.delete(IVAN.getId())).thenReturn(true);
        System.out.println("Before each: " + this.toString());


        Mockito.doReturn(true).when(userDao).delete(IVAN.getId());
// 2        Mockito.mock(UserDao.class, withSettings().lenient());
    }

    @Test
    void throwExceptionIfDatabaseIsNotAvailable() {
        Mockito.doThrow(RuntimeException.class).when(userDao).delete(IVAN.getId());
        assertThrows(RuntimeException.class, () -> userService.delete(IVAN.getId()));
    }

    @Test
    void shouldDeleteExistedUser() {
        userService.add(IVAN);
        Mockito.doReturn(true).when(userDao).delete(IVAN.getId());
        boolean deleteResult = userService.delete(IVAN.getId());
        System.out.println(deleteResult);
        System.out.println(userService.delete(IVAN.getId()));
        System.out.println(userService.delete(IVAN.getId()));
//        ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(userDao, Mockito.atLeast(2)).delete(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(IVAN.getId());
    }

}
