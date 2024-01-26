package com.telerikacademy.web.forumsystem.controllers;

import com.telerikacademy.web.forumsystem.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.forumsystem.helpers.AuthenticationHelper;
import com.telerikacademy.web.forumsystem.helpers.CommentMapper;
import com.telerikacademy.web.forumsystem.models.Comment;
import com.telerikacademy.web.forumsystem.models.CommentDto;
import com.telerikacademy.web.forumsystem.models.Post;
import com.telerikacademy.web.forumsystem.models.User;
import com.telerikacademy.web.forumsystem.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private AuthenticationHelper authenticationHelper;
    private CommentMapper commentMapper;
    private CommentService commentService;

    @Autowired
    public CommentController(AuthenticationHelper authenticationHelper, CommentMapper commentMapper, CommentService commentService) {
        this.authenticationHelper = authenticationHelper;
        this.commentMapper = commentMapper;
        this.commentService = commentService;
    }

    @PostMapping("/post/{postId}")
    public CommentDto commentOnPost(@RequestHeader HttpHeaders headers, @Valid @RequestBody CommentDto commentDto, @PathVariable int postId) {
        User author = authenticationHelper.tryGetUser(headers);
        Comment comment = commentMapper.fromDto(commentDto, author, postId);
        return commentMapper.toDto(comment);
    }

    @GetMapping("/post/{postId}")
    public List<CommentDto> getCommentsOnPost(@PathVariable int postId) {
        return commentService.getByPostId(postId)
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/comment/{commentId}")
    public List<CommentDto> getRepliesToComment(@PathVariable int commentId) {
        return commentService.getByCommentId(commentId)
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/comment/{commentId}")
    public CommentDto replyToComment(@RequestHeader HttpHeaders headers, @Valid @RequestBody CommentDto commentDto, @PathVariable int commentId) {
        User author = authenticationHelper.tryGetUser(headers);
        Comment comment = commentMapper.replyFromDto(commentDto, author, commentId);
        commentService.create(comment);
        return commentMapper.toDto(comment);
    }

}