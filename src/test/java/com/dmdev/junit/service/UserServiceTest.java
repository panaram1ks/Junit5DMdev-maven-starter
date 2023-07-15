package com.dmdev.junit.service;

import com.dmdev.junit.dto.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

class UserServiceTest {

    @Test
    void usersEmptyIfNoUserAdded() {
        var userService = new UserService();
        List<User> users = userService.getAll();
        assertFalse(users.isEmpty(), () -> "User list should be empty"); //  input ->  [box == function] -> actual output
    }
}
