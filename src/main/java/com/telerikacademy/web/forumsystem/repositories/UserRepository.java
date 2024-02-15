package com.telerikacademy.web.forumsystem.repositories;

import com.telerikacademy.web.forumsystem.models.FilterOptions;
import com.telerikacademy.web.forumsystem.models.PhoneNumber;
import com.telerikacademy.web.forumsystem.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserRepository {
    void create(User user);

    void delete(User user);

    void update(User user);

    void addPhone(PhoneNumber phoneNumber);


    List<User> get(FilterOptions filterOptions);
    Page<User> get(FilterOptions filterOptions, Pageable pageable);

    User getById(int id);

    List<User> getAll();

    User getByUsername(String username);

    User getByEmail(String email);

    boolean updateEmail(String email);
}
