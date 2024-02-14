package com.telerikacademy.web.forumsystem.repositories;

import com.telerikacademy.web.forumsystem.models.Post;
import com.telerikacademy.web.forumsystem.models.PostsFilterOptions;
import com.telerikacademy.web.forumsystem.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepository {
    void create (Post post);
    void update (Post post);
    void delete (Post post);
    Post getById (int id);
    List <Post> getAll();

    List<Post> get(PostsFilterOptions filterOptions);
    Page<Post> findUserPosts(User currentUser, Pageable pageable);
    List<Post> findMostRecentPosts(int limit);

    List<Post> findMostCommentedPosts(int limit);
}
