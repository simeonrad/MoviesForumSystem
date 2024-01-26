package com.telerikacademy.web.forumsystem.services;

import com.telerikacademy.web.forumsystem.models.User;

import java.util.List;

public interface UserService {
    void create (User user);
    void delete (User user, User deletedBy);
    void update (User user, User updatedBy);

    User getByName(String name, User user);

    User getByUsername (String username, User user);

    User getById (int id, User user);

    User getByEmail(String email, User user);

    void blockUser (String username, User admin);
    void unblockUser (String username, User admin);

    void makeAdmin (String username, User admin);
    void unmakeAdmin (String username, User admin);

    List<User> getAll();
}
