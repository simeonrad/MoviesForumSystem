package com.telerikacademy.web.forumsystem.services;

import com.telerikacademy.web.forumsystem.models.Comment;
import com.telerikacademy.web.forumsystem.models.User;

import java.util.List;

public interface CommentService {
    void create (Comment comment, User user);
    void update (Comment comment, User user);
    void delete (Comment comment, User user);
    Comment getById (int id);
    List<Comment> getByPostId(int id);
}
