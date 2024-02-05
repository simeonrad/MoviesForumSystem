package com.telerikacademy.web.forumsystem.services;

import com.telerikacademy.web.forumsystem.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.forumsystem.models.Post;
import com.telerikacademy.web.forumsystem.models.Tag;
import com.telerikacademy.web.forumsystem.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.telerikacademy.web.forumsystem.repositories.PostRepository;

import java.util.List;
import java.util.Set;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final TagService tagService;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, TagService tagService) {
        this.postRepository = postRepository;
        this.tagService = tagService;
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
        Post post = postRepository.getById(postId);

        if (post.getLikedByUsers().contains(user)) {
            post.getLikedByUsers().remove(user);
            post.setLikes(post.getLikes() - 1);
        } else {
            post.getLikedByUsers().add(user);
            post.setLikes(post.getLikes() + 1);
        }
        postRepository.update(post);
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
