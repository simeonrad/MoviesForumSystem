package com.telerikacademy.web.forumsystem.services;

import com.telerikacademy.web.forumsystem.models.Tag;
import com.telerikacademy.web.forumsystem.models.User;

public interface TagService {
    void create (String tag);
    void update (Tag tag, User user);
    void delete (Tag tag, User user);
    Tag getById(int id);
    Tag getByName(String name);
}
