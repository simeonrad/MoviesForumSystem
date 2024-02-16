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
    void createComment_WithUnblockedUser_Success() {
        User user = new User();
        user.setIsBlocked(false);
        Comment comment = new Comment();
        doNothing().when(commentRepository).create(any(Comment.class));
        commentService.create(comment, user);
        verify(commentRepository).create(comment);
    }

    @Test
    void createComment_WithBlockedUser_ThrowsException() {
        User user = new User();
        user.setIsBlocked(true);
        Comment comment = new Comment();
        assertThrows(UnauthorizedOperationException.class, () -> commentService.create(comment, user));
    }

    @Test
    void updateComment_ByAuthor_Success() {
        User author = new User();
        Comment comment = new Comment();
        comment.setAuthor(author);
        doNothing().when(commentRepository).update(any(Comment.class));
        commentService.update(comment, author);
        verify(commentRepository).update(comment);
    }

    @Test
    void updateComment_ByNonAuthor_ThrowsException() {
        User author = new User();
        author.setId(1);
        User nonAuthor = new User();
        nonAuthor.setId(2);
        Comment comment = new Comment();
        comment.setAuthor(author);
        assertThrows(UnauthorizedOperationException.class, () -> commentService.update(comment, nonAuthor));
    }

    @Test
    void deleteComment_ByAuthorOrAdmin_Success() {
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
    void deleteComment_ByNonAuthorNonAdmin_ThrowsException() {
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
    void getCommentById_Success() {
        int id = 1;
        Comment expectedComment = new Comment();
        when(commentRepository.getById(id)).thenReturn(expectedComment);
        Comment actualComment = commentService.getById(id);
        assertEquals(expectedComment, actualComment);
    }

    @Test
    void getCommentsByPostId_Success() {
        int postId = 1;
        List<Comment> expectedComments = Collections.singletonList(new Comment());
        when(commentRepository.getByPostId(postId)).thenReturn(expectedComments);
        List<Comment> actualComments = commentService.getByPostId(postId);
        assertEquals(expectedComments, actualComments);
    }

}
