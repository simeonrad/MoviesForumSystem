package com.telerikacademy.web.forumsystem.services;

import com.telerikacademy.web.forumsystem.models.Post;
import com.telerikacademy.web.forumsystem.models.User;


import java.util.List;

public interface PostService {
    void create (Post post, User user);
    void update (Post post, User user);
    void delete (Post post, User user);
    Post getById (int id);
    List<Post> getAll();
}
