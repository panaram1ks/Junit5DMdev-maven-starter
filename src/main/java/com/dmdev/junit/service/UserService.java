package com.dmdev.junit.service;

import com.dmdev.junit.dao.UserDao;
import com.dmdev.junit.dto.User;

import java.util.*;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class UserService {

    private final UserDao userDao;

    private final List<User> users = new ArrayList<>();

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean delete(Integer userId) {
        Integer userIdLocal = 25;
        return userDao.delete(userId);
    }

    public List<User> getAll() {
        return users;
    }

    public boolean add(User... users) {
        return this.users.addAll(Arrays.asList(users));
    }

    public Optional<User> login(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("username or password is null");
        }
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .filter(user -> user.getPassword().equals(password))
                .findFirst();
    }

    public Map<Integer, User> getAllConvertedById() {
        return users.stream().collect(toMap(User::getId, identity()));
    }
}
