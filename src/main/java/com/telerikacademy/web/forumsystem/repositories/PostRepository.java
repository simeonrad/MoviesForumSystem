package com.telerikacademy.web.forumsystem.repositories;

import com.telerikacademy.web.forumsystem.models.Post;

import java.util.List;

public interface PostRepository {
    void create (Post post);
    void update (Post post);
    void delete (Post post);
    Post getById (int id);
    List <Post> getAll();
}
