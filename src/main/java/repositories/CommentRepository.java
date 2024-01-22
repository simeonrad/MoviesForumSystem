package repositories;

import models.Comment;

import java.util.List;

public interface CommentRepository {
    void create (Comment comment);
    void update (Comment comment);
    void delete (Comment comment);
    Comment getById (int id);
    List<Comment> getByPostId(int id);
}
