package com.telerikacademy.web.forumsystem.controllers;

import com.telerikacademy.web.forumsystem.exceptions.EntityNotFoundException;
import com.telerikacademy.web.forumsystem.exceptions.NotAllowedContentException;
import com.telerikacademy.web.forumsystem.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.forumsystem.helpers.*;
import com.telerikacademy.web.forumsystem.models.*;
import com.telerikacademy.web.forumsystem.services.CommentService;
import com.telerikacademy.web.forumsystem.services.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;


@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final AuthenticationHelper authenticationHelper;
    private final TagHelper tagHelper;
    private final PostMapper postMapper;
    private final TextPurifier textPurifier;
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @Autowired
    public PostController(PostService postService, AuthenticationHelper authenticationHelper, PostMapper postMapper,
                          TagHelper tagHelper, TextPurifier textPurifier, CommentService commentService, CommentMapper commentMapper) {
        this.postService = postService;
        this.authenticationHelper = authenticationHelper;
        this.postMapper = postMapper;
        this.tagHelper = tagHelper;
        this.textPurifier = textPurifier;
        this.commentService = commentService;
        this.commentMapper = commentMapper;
    }

    @PostMapping()
    public PostDto createPost(@RequestBody PostDto postDto, @RequestHeader HttpHeaders headers) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            Post post = postMapper.fromDto(postDto);
            postService.create(post, currentUser);
            return postMapper.toDto(post);
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        }
    }

    @PutMapping("/post/{postId}")
    public PostDto updatePost(@PathVariable int postId, @RequestBody PostDto postDto, @RequestHeader HttpHeaders headers) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            Set<Tag> tags = tagHelper.tryGetTags(headers);
            Post post = postService.getById(postId);
            postMapper.updateFromDto(postDto, post);
            postService.update(post, currentUser, tags);
            return postMapper.toDto(post);
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable int postId, @RequestHeader HttpHeaders headers) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            Post post = postService.getById(postId);
            postService.delete(post, currentUser);
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/post/{postId}")
    public CommentDto commentOnPost(@RequestHeader HttpHeaders headers, @Valid @RequestBody CommentDto commentDto, @PathVariable int postId) {
        try {
            User author = authenticationHelper.tryGetUser(headers);
            Comment comment = commentMapper.fromDto(commentDto, author, postId);
            textPurifier.checkTextAndBan(comment.getContent(), author);
            commentService.create(comment, author);
            return commentMapper.toDto(comment);
        } catch (NotAllowedContentException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/post/{postId}")
    public List<CommentDto> getCommentsOnPost(@PathVariable int postId) {
        try {
            return commentService.getByPostId(postId)
                    .stream()
                    .map(commentMapper::toDto)
                    .collect(Collectors.toList());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{postId}/like")
    public void likePost(@PathVariable int postId, @RequestHeader HttpHeaders headers) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            postService.likePost(postId, currentUser);
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{postId}")
    public PostDto getPostById(HttpHeaders headers, @PathVariable int postId) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            postService.tryViewingPost(postId, user.getId());
            Post post = postService.getById(postId);
            return postMapper.toDto(post);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        catch (ResponseStatusException e){
            try {
                Post post = postService.getById(postId);
                return postMapper.toDto(post);
            }
            catch (EntityNotFoundException ee) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, ee.getMessage());
            }
        }
    }

    @GetMapping()
    public List<PostDto> getAllPosts() {
        List<Post> posts = postService.getAll();
        return postMapper.toDtoList(posts);
    }
}
