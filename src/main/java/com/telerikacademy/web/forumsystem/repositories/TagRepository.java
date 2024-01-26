package com.telerikacademy.web.forumsystem.repositories;

import com.telerikacademy.web.forumsystem.models.Tag;

public interface TagRepository {
    void create (Tag tag);
    void update (Tag tag);
    void delete (Tag tag);
    Tag getById(int id);
    Tag getByName(String name);
}
