package com.telerikacademy.web.forumsystem.repositories;

import com.telerikacademy.web.forumsystem.models.Comment;

import java.util.List;

public interface CommentRepository {
    void create (Comment comment);
    void update (Comment comment);
    void delete (Comment comment);
    Comment getById (int id);
    List<Comment> getByPostId(int id);
}
