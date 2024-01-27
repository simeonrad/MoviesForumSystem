package com.telerikacademy.web.forumsystem.repositories;

import com.telerikacademy.web.forumsystem.models.FilterOptions;
import com.telerikacademy.web.forumsystem.models.User;

import java.util.List;

public interface UserRepository {
    void create(User user);

    void delete(User user);

    void update(User user);

    List<User> get(FilterOptions filterOptions);

    User getById(int id);

    List<User> getAll();

    User getByUsername(String username);

    User getByEmail(String email);

    boolean updateEmail(String email);
}
