package com.telerikacademy.web.forumsystem.services;

import com.telerikacademy.web.forumsystem.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.forumsystem.models.Comment;
import com.telerikacademy.web.forumsystem.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.telerikacademy.web.forumsystem.repositories.CommentRepository;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService{

    private CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public void create(Comment comment) {
        commentRepository.create(comment);
    }

    @Override
    public void update(Comment comment, User user) {
        if (!comment.getAuthor().equals(user))
            throw new UnauthorizedOperationException("Only authors can edit their comments");
        commentRepository.update(comment);
    }

    @Override
    public void delete(Comment comment, User user) {
        if (!comment.getAuthor().equals(user))
            throw new UnauthorizedOperationException("Only authors can delete their comments");
        commentRepository.delete(comment);
    }

    @Override
    public Comment getById(int id) {
        return commentRepository.getById(id);
    }

    @Override
    public List<Comment> getByPostId(int id) {
        return commentRepository.getByPostId(id);
    }
    @Override
    public List<Comment> getByCommentId(int id) {
        return commentRepository.getByCommentId(id);
    }
}
