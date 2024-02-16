package com.telerikacademy.web.forumsystem.repositories;

import com.telerikacademy.web.forumsystem.models.Post;
import com.telerikacademy.web.forumsystem.models.Tag;

import java.util.List;
import java.util.Set;

public interface TagRepository {
    void create (Tag tag);
    void update (Tag tag);
    void delete (Tag tag);
    Tag getById(int id);
     List<Tag> getTagsForPost(Post post);
    Tag getByName(String name);
    void addTagsToPost(Set<Tag> tags, Post post);
}
