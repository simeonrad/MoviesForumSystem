package services;

import exceptions.UnauthorizedOperationException;
import models.Comment;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repositories.CommentRepository;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService{

    private CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public void create(Comment comment, User user) {
        comment.setAuthor(user);
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
}
