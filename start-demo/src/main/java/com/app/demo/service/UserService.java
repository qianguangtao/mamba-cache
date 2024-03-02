package com.app.demo.service;

import com.app.demo.entity.User;

import java.util.List;

public interface UserService {
    User insert(User user);

    void delete(String id);

    void delete(List<String> idList);

    User edit(User user);

    User getById(String id);

    List<User> list(User user);
}
