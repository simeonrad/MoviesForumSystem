package com.telerikacademy.web.forumsystem.services;

import com.telerikacademy.web.forumsystem.models.Post;
import com.telerikacademy.web.forumsystem.models.PostDto;
import com.telerikacademy.web.forumsystem.models.User;


import java.util.List;
import java.util.Set;

public interface PostService {
    void create (Post post, User user);
    void update (Post post, User user);
    void delete (Post post, User user);
    Post getById (int id);
    List<Post> getAll();
    void likePost(int postId, int userId);

    void addOrUpdatePost(Post post, Set<String> tagNames);
}
