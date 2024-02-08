package com.telerikacademy.web.forumsystem.services;

import com.telerikacademy.web.forumsystem.models.FilterOptions;
import com.telerikacademy.web.forumsystem.models.User;

import java.util.List;

public interface UserService {
    void create(User user);

    void delete(User user, User deletedBy);

    void update(User user, User updatedBy);

    List<User> get(FilterOptions filterOptions, User user);
    User get(String email);

    void blockUser(String username, User admin);

    void unblockUser(String username, User admin);

    void makeAdmin(String username, String phoneNumber, User admin);

    void unmakeAdmin(String username, User admin);

    List<User> getAll();
}
