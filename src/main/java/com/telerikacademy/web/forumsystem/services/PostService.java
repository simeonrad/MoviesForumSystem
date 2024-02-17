package com.telerikacademy.web.forumsystem.services;

import com.telerikacademy.web.forumsystem.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Set;

public interface PostService {
    void create(Post post, User user);

    void update(Post post, User user, Set<Tag> tags);

    void delete(Post post, User user);

    Post getById(int id);

    List<Post> getAll();

    List<Post> get(PostsFilterOptions filterOptions);
    Page<Post> get(PostsFilterOptions filterOptions, Pageable page);
    Page<Post> getMyPosts(PostsFilterOptions filterOptions, Pageable page);

    void likePost(int postId, User user);

    List<Post> getMostRecentPosts(int limit);

    List<Post> getMostCommentedPosts(int limit);

    Page<Post> getUsersPosts(User currentUser, int page, int size);
    void tryViewingPost(int postId, int userId);
}
