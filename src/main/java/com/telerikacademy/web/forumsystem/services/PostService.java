package com.telerikacademy.web.forumsystem.services;

import com.telerikacademy.web.forumsystem.models.Post;
import com.telerikacademy.web.forumsystem.models.Tag;
import com.telerikacademy.web.forumsystem.models.User;


import java.util.List;
import java.util.Set;

public interface PostService {
    void create(Post post, User user);

    void update(Post post, User user, Set<Tag> tags);

    void delete(Post post, User user);

    Post getById(int id);

    List<Post> getAll();

    void likePost(int postId, User user);

    public List<Post> getMostRecentPosts(int limit);

    public List<Post> getMostCommentedPosts(int limit);
}
