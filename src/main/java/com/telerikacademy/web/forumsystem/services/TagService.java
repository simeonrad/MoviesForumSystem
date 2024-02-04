package com.telerikacademy.web.forumsystem.services;

import com.telerikacademy.web.forumsystem.models.Post;
import com.telerikacademy.web.forumsystem.models.Tag;

import java.util.Set;

public interface TagService {
    void create (String tag);
    void update (Tag tag);
    void delete (Tag tag);
    void addTagsToPost(Set<Tag> tags, Post post);
    Tag getById(int id);
    Tag getByName(String name);
}
