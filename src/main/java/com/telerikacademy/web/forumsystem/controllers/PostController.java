package com.telerikacademy.web.forumsystem.controllers;

import com.telerikacademy.web.forumsystem.exceptions.EntityNotFoundException;
import com.telerikacademy.web.forumsystem.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.forumsystem.helpers.AuthenticationHelper;
import com.telerikacademy.web.forumsystem.helpers.PostMapper;
import com.telerikacademy.web.forumsystem.models.Post;
import com.telerikacademy.web.forumsystem.models.PostDto;
import com.telerikacademy.web.forumsystem.models.User;
import com.telerikacademy.web.forumsystem.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final AuthenticationHelper authenticationHelper;
    private final PostMapper postMapper;

    @Autowired
    public PostController(PostService postService, AuthenticationHelper authenticationHelper, PostMapper postMapper) {
        this.postService = postService;
        this.authenticationHelper = authenticationHelper;
        this.postMapper = postMapper;
    }

    @PostMapping()
    public PostDto createPost(@RequestBody PostDto postDto, @RequestHeader HttpHeaders headers) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            Set<String> tags = extractTagNamesFromHeader(headers);
            Post post = postMapper.fromDto(postDto);
            postService.addOrUpdatePost(post, tags);
            return postMapper.toDto(post);
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        }
    }

    private Set<String> extractTagNamesFromHeader(HttpHeaders headers) {
        String tagHeader = headers.getFirst("tags");
        if (tagHeader != null && !tagHeader.isEmpty()) {
            return new HashSet<>(Arrays.asList(tagHeader.split(",")));
        }
        return new HashSet<>();
    }

    @PutMapping("/{postId}")
    public PostDto updatePost(@PathVariable int postId, @RequestBody PostDto postDto, @RequestHeader HttpHeaders headers) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            Post post = postService.getById(postId);
            if (post == null) {
                throw new EntityNotFoundException("Post", postId);
            }
            postMapper.updateFromDto(postDto, post);
            postService.update(post, currentUser);
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
            if (post == null) {
                throw new EntityNotFoundException("Post", postId);
            }
            postService.delete(post, currentUser);
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{postId}/like")
    public void likePost(@PathVariable int postId, @RequestHeader HttpHeaders headers) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            postService.likePost(postId, currentUser.getId());
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{postId}")
    public PostDto getPostById(@PathVariable int postId) {
        try {
            Post post = postService.getById(postId);
            if (post == null) {
                throw new EntityNotFoundException("Post", postId);
            }
            return postMapper.toDto(post);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping()
    public List<PostDto> getAllPosts() {
        List<Post> posts = postService.getAll();
        return postMapper.toDtoList(posts);
    }
}
