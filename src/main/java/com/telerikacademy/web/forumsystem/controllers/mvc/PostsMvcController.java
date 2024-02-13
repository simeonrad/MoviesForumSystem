package com.telerikacademy.web.forumsystem.controllers.mvc;

import com.telerikacademy.web.forumsystem.exceptions.AuthenticationFailureException;
import com.telerikacademy.web.forumsystem.exceptions.EntityNotFoundException;
import com.telerikacademy.web.forumsystem.exceptions.NotAllowedContentException;
import com.telerikacademy.web.forumsystem.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.forumsystem.helpers.AuthenticationHelper;
import com.telerikacademy.web.forumsystem.helpers.CommentMapper;
import com.telerikacademy.web.forumsystem.helpers.TextPurifier;
import com.telerikacademy.web.forumsystem.models.Comment;
import com.telerikacademy.web.forumsystem.models.CommentDto;
import com.telerikacademy.web.forumsystem.models.Post;
import com.telerikacademy.web.forumsystem.models.User;
import com.telerikacademy.web.forumsystem.repositories.CommentRepository;
import com.telerikacademy.web.forumsystem.repositories.LikeRepository;
import com.telerikacademy.web.forumsystem.repositories.View_Repository;
import com.telerikacademy.web.forumsystem.services.CommentService;
import com.telerikacademy.web.forumsystem.services.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequestMapping("/posts")
public class PostsMvcController {
    private final PostService postService;
    private final View_Repository viewRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CommentService commentService;
    private final AuthenticationHelper authenticationHelper;
    private final LikeRepository likeRepository;
    private final TextPurifier textPurifier;

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @Autowired
    public PostsMvcController(PostService postService, View_Repository viewRepository, CommentRepository commentRepository, CommentMapper commentMapper, CommentService commentService, AuthenticationHelper authenticationHelper, LikeRepository likeRepository, TextPurifier textPurifier) {
        this.postService = postService;
        this.viewRepository = viewRepository;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.commentService = commentService;
        this.authenticationHelper = authenticationHelper;
        this.likeRepository = likeRepository;
        this.textPurifier = textPurifier;
    }

    @GetMapping("/{id}")
    public String singlePostView(Model model, @PathVariable int id, HttpSession session) {
        try {
            postService.getById(id);
            User user = authenticationHelper.tryGetUser(session);
            postService.tryViewingPost(id, user.getId());
        } catch (AuthenticationFailureException ignored) {
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
        try {
            Post post = postService.getById(id);
            List<Comment> comments = commentRepository.getByPostId(id);
            model.addAttribute("post", post);
            model.addAttribute("likesCount", likeRepository.getLikesCountOnPost(id));
            model.addAttribute("commentDto", new CommentDto());
            model.addAttribute("viewCount", viewRepository.getViewsCountOnPost(id));
            model.addAttribute("post_comments", comments);
            return "PostView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }

    }
    @PostMapping("/{id}/delete")
    public String deletePost(Model model, @PathVariable int id, HttpSession session) {
        try {
           Post post = postService.getById(id);
            User user = authenticationHelper.tryGetUser(session);
            postService.delete(post, user);
            return "index";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
        catch (UnauthorizedOperationException e){
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }
    @PostMapping("/{id}/update")
    public String updatePost(Model model, @PathVariable int id, HttpSession session) {
        try {
           Post post = postService.getById(id);
            User user = authenticationHelper.tryGetUser(session);
            //postService.update(post, user);
            return "index";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
        catch (UnauthorizedOperationException e){
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @PostMapping("/{id}")
    public String commentOnPost(@Valid @ModelAttribute("comment") CommentDto commentDto,
                                Model model, HttpSession session,
                                @PathVariable int id) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
        try {
            textPurifier.checkTextAndBan(commentDto.getContent(), user);
            Comment comment = commentMapper.fromDto(commentDto, user, id);
            commentService.create(comment, user);
            return "redirect:/posts/" + id;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
        catch (NotAllowedContentException e){
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "bannedView";
        }
        catch (UnauthorizedOperationException e){
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @PostMapping("/{id}/like")
    public String likePost(Model model, HttpSession session, @PathVariable int id) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
        try {
            postService.likePost(id, user);
            return "redirect:/posts/" + id;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @PostMapping("/{Id}/reply")
    public String postCommentReply(@Valid @ModelAttribute("reply") CommentDto commentDto,
                                   HttpSession session,
                                   Model model,
                                   RedirectAttributes redirectAttributes,
                                   @PathVariable int Id,
                                   HttpServletRequest request) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            int commentId = Integer.parseInt(request.getParameter("commentId"));
            Comment comment = commentService.getById(commentId);
            Comment reply = commentMapper.replyFromDto(commentDto, user, commentId);
            commentService.create(reply, user);
            comment.addReply(reply);
            redirectAttributes.addFlashAttribute("message", "Reply posted successfully.");
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
        catch (UnauthorizedOperationException e){
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }

        return "redirect:/posts/" + Id;
    }
}
