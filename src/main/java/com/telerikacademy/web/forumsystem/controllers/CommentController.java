package com.telerikacademy.web.forumsystem.controllers;

import com.telerikacademy.web.forumsystem.exceptions.EntityNotFoundException;
import com.telerikacademy.web.forumsystem.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.forumsystem.helpers.AuthenticationHelper;
import com.telerikacademy.web.forumsystem.helpers.CommentMapper;
import com.telerikacademy.web.forumsystem.models.Comment;
import com.telerikacademy.web.forumsystem.models.CommentDto;
import com.telerikacademy.web.forumsystem.models.User;
import com.telerikacademy.web.forumsystem.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comments")
public class CommentController {

    public static final String YOUR_ACCOUNT_IS_BLOCKED = "Your account is blocked you are not allowed to comment or reply";
    private AuthenticationHelper authenticationHelper;
    private CommentMapper commentMapper;
    private CommentService commentService;

    @Autowired
    public CommentController(AuthenticationHelper authenticationHelper, CommentMapper commentMapper,
                             CommentService commentService) {
        this.authenticationHelper = authenticationHelper;
        this.commentMapper = commentMapper;
        this.commentService = commentService;
    }

    @PostMapping("/post/{postId}")
    public CommentDto commentOnPost(@RequestHeader HttpHeaders headers, @Valid @RequestBody CommentDto commentDto, @PathVariable int postId) {
        try {
            User author = authenticationHelper.tryGetUser(headers);
            if (author.getIsBlocked()){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, YOUR_ACCOUNT_IS_BLOCKED);
            }
            Comment comment = commentMapper.fromDto(commentDto, author, postId);
            return commentMapper.toDto(comment);
        }
        catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }    }

    @GetMapping("/post/{postId}")
    public List<CommentDto> getCommentsOnPost(@PathVariable int postId) {
        try {
            return commentService.getByPostId(postId)
                    .stream()
                    .map(commentMapper::toDto)
                    .collect(Collectors.toList());
        }
        catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/comment/{commentId}")
    public List<CommentDto> getRepliesToComment(@PathVariable int commentId) {
        try {
            return commentService.getByCommentId(commentId)
                    .stream()
                    .map(commentMapper::toDto)
                    .collect(Collectors.toList());

        }
        catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/comment/{commentId}")
    public CommentDto replyToComment(@RequestHeader HttpHeaders headers, @Valid @RequestBody CommentDto commentDto, @PathVariable int commentId) {
       try {
           User author = authenticationHelper.tryGetUser(headers);
           if (author.getIsBlocked()) {
               throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, YOUR_ACCOUNT_IS_BLOCKED);
           }
           Comment comment = commentMapper.replyFromDto(commentDto, author, commentId);
           commentService.create(comment);
           return commentMapper.toDto(comment);
       }
       catch (EntityNotFoundException e){
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
       }
    }
   @DeleteMapping("/comment/{commentId}")
    public void deleteComment(@RequestHeader HttpHeaders headers, @PathVariable int commentId) {
       try {
           User user = authenticationHelper.tryGetUser(headers);
           Comment comment = commentService.getById(commentId);
           commentService.delete(comment, user);
       }
       catch (UnauthorizedOperationException e) {
           throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
       }
       catch (EntityNotFoundException e){
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
       }
    }
}