package com.telerikacademy.web.forumsystem.helpers;

import com.telerikacademy.web.forumsystem.models.Comment;
import com.telerikacademy.web.forumsystem.models.CommentDto;
import com.telerikacademy.web.forumsystem.models.User;
import com.telerikacademy.web.forumsystem.models.UserDto;
import com.telerikacademy.web.forumsystem.repositories.CommentRepository;
import com.telerikacademy.web.forumsystem.repositories.PostRepository;
import com.telerikacademy.web.forumsystem.repositories.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    private PostRepository postRepository;
    private CommentRepository commentRepository;

    public CommentMapper(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public Comment fromDto(int id, CommentDto commentDto, User author, int postId) {
        Comment comment = fromDto(commentDto, author, postId);
        comment.setId(id);
        return comment;
    }

    public Comment fromDto(CommentDto commentDto, User author, int postId) {
       Comment comment = new Comment();
       comment.setContent(commentDto.getContent());
       comment.setAuthor(author);
       comment.setPost(postId);
        return comment;
    }    public Comment replyFromDto(CommentDto commentDto, User author, int commentId) {
       Comment comment = new Comment();
       comment.setContent(commentDto.getContent());
       comment.setAuthor(author);
       comment.setRepliedTo(commentRepository.getById(commentId));
        return comment;
    }


    public CommentDto toDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setContent(comment.getContent());
        return commentDto;
    }

}
