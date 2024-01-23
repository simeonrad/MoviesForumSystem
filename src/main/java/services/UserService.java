package services;

import models.User;

import java.util.List;

public interface UserService {
    void create (User user, User createdBy);
    void delete (User user, User deletedBy);
    void update (User user, User updatedBy);

    User getByName(String name);

    User getByUsername (String username);

    User getById (int id);

    List<User> getAll();
}
