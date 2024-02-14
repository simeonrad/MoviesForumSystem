package com.telerikacademy.web.forumsystem.services;

import com.telerikacademy.web.forumsystem.exceptions.EntityNotFoundException;
import com.telerikacademy.web.forumsystem.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.forumsystem.models.*;
import com.telerikacademy.web.forumsystem.repositories.LikeRepository;
import com.telerikacademy.web.forumsystem.repositories.LikeRepositoryImpl;
import com.telerikacademy.web.forumsystem.repositories.View_Repository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.telerikacademy.web.forumsystem.repositories.PostRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final TagService tagService;
    private final LikeRepository likeRepository;
    private final View_Repository viewRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, TagService tagService, LikeRepositoryImpl likeRepository, View_Repository viewRepository) {
        this.postRepository = postRepository;
        this.tagService = tagService;
        this.likeRepository = likeRepository;
        this.viewRepository = viewRepository;
    }

    @Override
    public void create(Post post, User user) {
        post.setAuthor(user);
        postRepository.create(post);
    }

    @Override
    public void update(Post post, User user, Set<Tag> tags) {
        if (!post.getAuthor().equals(user)) {
            throw new UnauthorizedOperationException("Only the author can modify it's data!");
        } else {
            postRepository.update(post);
            if (tags != null) {
                tagService.addTagsToPost(tags, post);
            }
        }
    }

    @Override
    public void delete(Post post, User user) {
        if (!post.getAuthor().equals(user) && !user.isAdmin()) {
            throw new UnauthorizedOperationException("Only the author can modify it's data!");
        } else {
            postRepository.delete(post);
        }
    }

    public void likePost(int postId, User user) {
        try {
            likeRepository.getByPostAndUserId(postId, user.getId());
            likeRepository.unlikeAPost(postId, user.getId());
        } catch (EntityNotFoundException e) {
            likeRepository.likeAPost(postId, user.getId());
        }
    }

    @Override
    @Transactional
    public void tryViewingPost(int postId, int userId) {
        try {
            PostView view = viewRepository.getMostRecentViewByPostAndUserId(postId, userId);
            if (!view.getTimestamp().plusMinutes(5).isAfter(LocalDateTime.now())) {
                viewRepository.viewAPost(postId, userId);
            }
        } catch (EntityNotFoundException e) {
            viewRepository.viewAPost(postId, userId);
        }
    }

    @Override
    public Post getById(int id) {
        return postRepository.getById(id);
    }

    @Override
    public List<Post> getAll() {
        return postRepository.getAll();
    }

    @Override
    public List<Post> get(PostsFilterOptions filterOptions) {
        return postRepository.get(filterOptions);
    }

    @Override
    public List<Post> getMostRecentPosts(int limit) {
        return postRepository.findMostRecentPosts(limit);
    }

    @Override
    public List<Post> getMostCommentedPosts(int limit) {
        return postRepository.findMostCommentedPosts(limit);
    }
}
