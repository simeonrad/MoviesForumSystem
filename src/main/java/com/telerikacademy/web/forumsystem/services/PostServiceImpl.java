package com.telerikacademy.web.forumsystem.services;

import com.telerikacademy.web.forumsystem.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.forumsystem.models.Post;
import com.telerikacademy.web.forumsystem.models.User;
import com.telerikacademy.web.forumsystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.telerikacademy.web.forumsystem.repositories.PostRepository;

import java.util.List;

@Service
public class PostServiceImpl implements PostService{
   private PostRepository postRepository;
   private UserRepository userRepository;

   @Autowired
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public void create(Post post, User user) {
       post.setAuthor(user);
       postRepository.create(post);
    }

    @Override
    public void update(Post post, User user) {
        if (!post.getAuthor().equals(user)) {
            throw new UnauthorizedOperationException("Only the user can modify it's data!");
        }
        else {
            postRepository.update(post);
        }
    }

    @Override
    public void delete(Post post, User user) {
        if (!post.getAuthor().equals(user) || !user.isAdmin()) {
            throw new UnauthorizedOperationException("Only the user can modify it's data!");
        }
        else {
            postRepository.delete(post);
        }
    }

    public void likePost(int postId, int userId) {
        Post post = postRepository.getById(postId);
        User user = userRepository.getById(userId);

        if (post.getLikedByUsers().contains(user)) {
            post.getLikedByUsers().remove(user);
        }

        post.getLikedByUsers().add(user);
        post.setLikes(post.getLikes() + 1);
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
}
