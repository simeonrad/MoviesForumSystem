package com.telerikacademy.web.forumsystem.services;

import com.telerikacademy.web.forumsystem.models.FilterOptions;
import com.telerikacademy.web.forumsystem.models.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    void create(User user);
    void delete(User user, User deletedBy);
    void delete(User user);
    void update(User user, User updatedBy);
    void update(User user);

    List<User> get(FilterOptions filterOptions, User user);
    Page<User> get(FilterOptions filterOptions, int page, int size);
    User get(String email);

    void blockUser(String username, User admin);

    void unblockUser(String username, User admin);

    void makeAdmin(String username, String phoneNumber, User admin);

    void addPhoneNumber(String phoneNumber, User user);

    void addProfilePhoto(String photoUrl, User user);

    void makeAdmin(String username);

    void unmakeAdmin(String username, User admin);

    List<User> getAll();
    List<User> getAllNotDeleted();
}
