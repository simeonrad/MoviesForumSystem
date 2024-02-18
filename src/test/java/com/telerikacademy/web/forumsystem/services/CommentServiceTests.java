package com.telerikacademy.web.forumsystem.services;
import com.telerikacademy.web.forumsystem.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.forumsystem.models.Comment;
import com.telerikacademy.web.forumsystem.models.User;
import com.telerikacademy.web.forumsystem.repositories.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTests {
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private CommentServiceImpl commentService;
    @Test
    public void createComment_WithUnblockedUser_Success() {
        User user = new User();
        user.setIsBlocked(false);
        Comment comment = new Comment();
        doNothing().when(commentRepository).create(any(Comment.class));
        commentService.create(comment, user);
        verify(commentRepository).create(comment);
    }

    @Test
    public void createComment_WithBlockedUser_ThrowsException() {
        User user = new User();
        user.setIsBlocked(true);
        Comment comment = new Comment();
        assertThrows(UnauthorizedOperationException.class, () -> commentService.create(comment, user));
    }

    @Test
    public void updateComment_ByAuthor_Success() {
        User author = new User();
        Comment comment = new Comment();
        comment.setAuthor(author);
        doNothing().when(commentRepository).update(any(Comment.class));
        commentService.update(comment, author);
        verify(commentRepository).update(comment);
    }

    @Test
    public void updateComment_ByNonAuthor_ThrowsException() {
        User author = new User();
        author.setId(1);
        User nonAuthor = new User();
        nonAuthor.setId(2);
        Comment comment = new Comment();
        comment.setAuthor(author);
        assertThrows(UnauthorizedOperationException.class, () -> commentService.update(comment, nonAuthor));
    }

    @Test
    public void deleteComment_ByAuthorOrAdmin_Success() {
        User author = new User();
        User admin = new User();
        admin.setAdmin(true);
        Comment comment = new Comment();
        comment.setAuthor(author);
        commentService.delete(comment, author);
        commentService.delete(comment, admin);
        verify(commentRepository, times(2)).delete(comment);
    }

    @Test
    public void deleteComment_ByNonAuthorNonAdmin_ThrowsException() {
        User author = new User();
        author.setId(1);
        User nonAuthorNonAdmin = new User();
        nonAuthorNonAdmin.setId(2);
        nonAuthorNonAdmin.setAdmin(false);
        Comment comment = new Comment();
        comment.setAuthor(author);
        assertThrows(UnauthorizedOperationException.class, () -> commentService.delete(comment, nonAuthorNonAdmin));
    }

    @Test
    public void getCommentById_Success() {
        int id = 1;
        Comment expectedComment = new Comment();
        when(commentRepository.getById(id)).thenReturn(expectedComment);
        Comment actualComment = commentService.getById(id);
        assertEquals(expectedComment, actualComment);
    }

    @Test
    public void getCommentsByPostId_Success() {
        int postId = 1;
        List<Comment> expectedComments = Collections.singletonList(new Comment());
        when(commentRepository.getByPostId(postId)).thenReturn(expectedComments);
        List<Comment> actualComments = commentService.getByPostId(postId);
        assertEquals(expectedComments, actualComments);
    }

    @Test
    public void testGetUserComments() {
        // Given
        User user = new User();
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Comment comment1 = new Comment();
        Comment comment2 = new Comment();
        List<Comment> comments = Arrays.asList(comment1, comment2);
        Page<Comment> pageOfComments = new PageImpl<>(comments);
        when(commentRepository.getUserComments(eq(user), any(Pageable.class))).thenReturn(pageOfComments);

        // When
        Page<Comment> resultPage = commentService.getUserComments(user, page, size);

        // Then
        assertNotNull(resultPage);
        assertEquals(2, resultPage.getContent().size());
        verify(commentRepository).getUserComments(eq(user), any(Pageable.class));
    }

    @Test
    public void testGetAll() {
        // Given
        Comment comment1 = new Comment(); // Assuming Comment is a class with appropriate fields
        Comment comment2 = new Comment();
        List<Comment> mockComments = Arrays.asList(comment1, comment2);
        when(commentRepository.getAll()).thenReturn(mockComments);

        // When
        List<Comment> result = commentService.getAll();

        // Then
        verify(commentRepository, times(1)).getAll();
    }

}
