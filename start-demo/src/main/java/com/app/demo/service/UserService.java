package com.app.demo.service;

import com.app.demo.entity.User;

import java.util.List;

public interface UserService {
    User insert(User user);

    void delete(Long id);

    User edit(User user);

    User getById(Long id);

    List<User> list(User user);
}
