package com.telerikacademy.web.forumsystem.repositories;

import com.telerikacademy.web.forumsystem.models.Comment;
import com.telerikacademy.web.forumsystem.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentRepository {
    void create (Comment comment);
    void update (Comment comment);
    void delete (Comment comment);
    Comment getById (int id);
    List<Comment> getByPostId(int id);
    List<Comment> getByCommentId(int id);
    Page<Comment> getUserComments(User user, Pageable pageable);
}
