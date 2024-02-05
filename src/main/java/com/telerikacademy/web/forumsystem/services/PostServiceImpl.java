package com.telerikacademy.web.forumsystem.services;

import com.telerikacademy.web.forumsystem.exceptions.EntityNotFoundException;
import com.telerikacademy.web.forumsystem.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.forumsystem.models.Post;
import com.telerikacademy.web.forumsystem.models.Tag;
import com.telerikacademy.web.forumsystem.models.User;
import com.telerikacademy.web.forumsystem.repositories.LikeRepository;
import com.telerikacademy.web.forumsystem.repositories.LikeRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.telerikacademy.web.forumsystem.repositories.PostRepository;

import java.util.List;
import java.util.Set;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final TagService tagService;
    private final LikeRepository likeRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, TagService tagService, LikeRepositoryImpl likeRepository) {
        this.postRepository = postRepository;
        this.tagService = tagService;
        this.likeRepository = likeRepository;
    }

    @Override
    public void create(Post post, User user) {
        post.setAuthor(user);
        postRepository.create(post);
    }

    @Override
    public void update(Post post, User user, Set<Tag> tags) {
        if (!post.getAuthor().equals(user)) {
            throw new UnauthorizedOperationException("Only the user can modify it's data!");
        } else {
            postRepository.update(post);
            if (tags != null) {
                tagService.addTagsToPost(tags, post);
            }
        }
    }

    @Override
    public void delete(Post post, User user) {
        if (!post.getAuthor().equals(user) || !user.isAdmin()) {
            throw new UnauthorizedOperationException("Only the user can modify it's data!");
        } else {
            postRepository.delete(post);
        }
    }

    public void likePost(int postId, User user) {
        try {
           likeRepository.getByPostAndUserId(postId, user.getId());
           likeRepository.unlikeAPost(postId, user.getId());
        }
        catch (EntityNotFoundException e){
            likeRepository.likeAPost(postId, user.getId());
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
    public List<Post> getMostRecentPosts(int limit) {
        return postRepository.findMostRecentPosts(limit);
    }

    @Override
    public List<Post> getMostCommentedPosts(int limit) {
        return postRepository.findMostCommentedPosts(limit);
    }
}
