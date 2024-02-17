package com.telerikacademy.web.forumsystem.services;

import com.telerikacademy.web.forumsystem.exceptions.EntityNotFoundException;
import com.telerikacademy.web.forumsystem.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.forumsystem.models.*;
import com.telerikacademy.web.forumsystem.repositories.LikeRepository;
import com.telerikacademy.web.forumsystem.repositories.PostRepository;
import com.telerikacademy.web.forumsystem.repositories.View_Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTests {

    @Mock
    private PostRepository postRepository;

    @Mock
    private TagService tagService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private View_Repository viewRepository;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    void createPost_WithValidUser_CreatesPostSuccessfully() {
        User user = new User();
        Post post = new Post();
        doNothing().when(postRepository).create(post);
        postService.create(post, user);
        verify(postRepository).create(post);
        assertEquals(user, post.getAuthor());
    }

    @Test
    void updatePost_ByAuthor_UpdatesPostSuccessfully() {
        User user = new User();
        Post post = new Post();
        post.setAuthor(user);
        Set<Tag> tags = new HashSet<>();
        doNothing().when(postRepository).update(post);
        postService.update(post, user, tags);
        verify(postRepository).update(post);
    }

    @Test
    void updatePost_ByNonAuthor_ThrowsUnauthorizedOperationException() {
        User user = new User();
        User nonAuthor = new User();
        user.setId(1);
        nonAuthor.setId(2);
        Post post = new Post();
        post.setAuthor(user);
        Set<Tag> tags = new HashSet<>();
        assertThrows(UnauthorizedOperationException.class, () -> postService.update(post, nonAuthor, tags));
    }

    @Test
    void deletePost_ByAuthor_DeletesPostSuccessfully() {
        User user = new User();
        Post post = new Post();
        post.setAuthor(user);
        doNothing().when(postRepository).delete(post);
        postService.delete(post, user);
        verify(postRepository).delete(post);
    }

    @Test
    void deletePost_ByNonAuthor_ThrowsUnauthorizedOperationException() {
        User user = new User();
        User nonAuthor = new User();
        user.setId(1);
        nonAuthor.setId(2);
        Post post = new Post();
        post.setAuthor(user);
        assertThrows(UnauthorizedOperationException.class, () -> postService.delete(post, nonAuthor));
    }

    @Test
    void getPostById_ExistingId_ReturnsPost() {
        int id = 1;
        Post expectedPost = new Post();
        when(postRepository.getById(id)).thenReturn(expectedPost);
        Post actualPost = postService.getById(id);
        assertEquals(expectedPost, actualPost);
    }

    @Test
    void likePost_WhenNotLikedBefore_LikesPost() {
        int postId = 1;
        User user = new User();
        user.setId(2);
        when(likeRepository.getByPostAndUserId(postId, user.getId())).thenThrow(new EntityNotFoundException(""));
        postService.likePost(postId, user);
        verify(likeRepository).likeAPost(postId, user.getId());
    }

    @Test
    void likePost_WhenAlreadyLiked_UnlikesPost() {
        int postId = 1;
        User user = new User();
        user.setId(2);
        doNothing().when(likeRepository).unlikeAPost(postId, user.getId());
        postService.likePost(postId, user);
        verify(likeRepository).unlikeAPost(postId, user.getId());
    }

    @Test
    void tryViewingPost_WhenViewedRecently_DoesNotAddNewView() {
        int postId = 1, userId = 2;
        PostView recentView = new PostView();
        recentView.setTimestamp(LocalDateTime.now().minusMinutes(1));
        when(viewRepository.getMostRecentViewByPostAndUserId(postId, userId)).thenReturn(recentView);
        postService.tryViewingPost(postId, userId);
        verify(viewRepository, never()).viewAPost(postId, userId);
    }

    @Test
    void tryViewingPost_WhenNotViewedRecently_AddsNewView() {
        int postId = 1, userId = 2;
        PostView oldView = new PostView();
        oldView.setTimestamp(LocalDateTime.now().minusHours(1));
        when(viewRepository.getMostRecentViewByPostAndUserId(postId, userId)).thenReturn(oldView);
        postService.tryViewingPost(postId, userId);
        verify(viewRepository).viewAPost(postId, userId);
    }

    @Test
    void tryViewingPost_WhenNeverViewed_AddsNewView() {
        int postId = 1, userId = 2;
        when(viewRepository.getMostRecentViewByPostAndUserId(postId, userId)).thenThrow(new EntityNotFoundException(""));
        postService.tryViewingPost(postId, userId);
        verify(viewRepository).viewAPost(postId, userId);
    }

    @Test
    void getAll_ReturnsAllPosts() {
        List<Post> expectedPosts = List.of(new Post(), new Post());
        when(postRepository.getAll()).thenReturn(expectedPosts);
        List<Post> actualPosts = postService.getAll();
        assertEquals(expectedPosts, actualPosts);
        verify(postRepository).getAll();
    }

    @Test
    void getWithFilterOptionsAndPagination_ReturnsCorrectPageOfPosts() {
        PostsFilterOptions options = new PostsFilterOptions("Sample Title", null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> expectedPage = new PageImpl<>(List.of(new Post()), pageable, 1);
        when(postRepository.get(options, pageable)).thenReturn(expectedPage);
        Page<Post> actualPage = postService.get(options, pageable);
        assertEquals(expectedPage.getTotalElements(), actualPage.getTotalElements());
        verify(postRepository).get(options, pageable);
    }

    @Test
    void getMyPosts_ReturnsCorrectPostsForUser() {
        PostsFilterOptions options = new PostsFilterOptions(null, null, "userCreator", null, "createdAt", "desc");
        Pageable pageable = PageRequest.of(0, 10);
        User currentUser = new User();
        currentUser.setUsername("userCreator");
        currentUser.setId(1);
        List<Post> postsList = List.of(new Post());
        Page<Post> expectedPage = new PageImpl<>(postsList, pageable, postsList.size());
        when(postRepository.getMyPosts(options, pageable)).thenReturn(expectedPage);
        Page<Post> actualPage = postService.getMyPosts(options, pageable);
        assertNotNull(actualPage);
        assertEquals(expectedPage.getTotalElements(), actualPage.getTotalElements(), "The expected number of elements should match");
        assertEquals(expectedPage.getContent(), actualPage.getContent(), "The content of the pages should match");
        verify(postRepository).getMyPosts(options, pageable);
    }

    @Test
    void getMostRecentPosts_ReturnsLimitedNumberOfRecentPosts() {
        int limit = 2;
        List<Post> expectedPosts = List.of(new Post(), new Post());
        when(postRepository.findMostRecentPosts(limit)).thenReturn(expectedPosts);
        List<Post> actualPosts = postService.getMostRecentPosts(limit);
        assertEquals(expectedPosts.size(), actualPosts.size(), "Should return the correct number of posts");
        assertEquals(expectedPosts, actualPosts, "Should return the expected recent posts");
        verify(postRepository).findMostRecentPosts(limit);
    }

    @Test
    void getMostCommentedPosts_ReturnsLimitedNumberOfCommentedPosts() {
        int limit = 2;
        List<Post> expectedPosts = List.of(new Post(), new Post());
        when(postRepository.findMostCommentedPosts(limit)).thenReturn(expectedPosts);
        List<Post> actualPosts = postService.getMostCommentedPosts(limit);
        assertEquals(expectedPosts.size(), actualPosts.size(), "Should return the correct number of posts");
        assertEquals(expectedPosts, actualPosts, "Should return the expected most commented posts");
        verify(postRepository).findMostCommentedPosts(limit);
    }

    @Test
    void getUsersPosts_ReturnsPageOfPostsForCurrentUser() {
        User currentUser = new User();
        currentUser.setId(1);
        int page = 0, size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> expectedPage = new PageImpl<>(List.of(new Post()), pageable, 1);
        when(postRepository.findUserPosts(currentUser, pageable)).thenReturn(expectedPage);
        Page<Post> actualPage = postService.getUsersPosts(currentUser, page, size);
        assertEquals(expectedPage.getTotalElements(), actualPage.getTotalElements(), "Should match total elements");
        assertEquals(expectedPage.getContent(), actualPage.getContent(), "Should return the expected posts for the user");
        verify(postRepository).findUserPosts(currentUser, pageable);
    }

}
