package com.telerikacademy.web.forumsystem.controllers.mvc;

import com.telerikacademy.web.forumsystem.exceptions.AuthenticationFailureException;
import com.telerikacademy.web.forumsystem.exceptions.EntityNotFoundException;
import com.telerikacademy.web.forumsystem.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.forumsystem.helpers.AuthenticationHelper;
import com.telerikacademy.web.forumsystem.helpers.CommentMapper;
import com.telerikacademy.web.forumsystem.models.Comment;
import com.telerikacademy.web.forumsystem.models.CommentDto;
import com.telerikacademy.web.forumsystem.models.Post;
import com.telerikacademy.web.forumsystem.models.User;
import com.telerikacademy.web.forumsystem.repositories.CommentRepository;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/posts")
public class PostsMvcController {
    private final PostService postService;
    private final View_Repository viewRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CommentService commentService;
    private final AuthenticationHelper authenticationHelper;
    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @Autowired
    public PostsMvcController(PostService postService, View_Repository viewRepository, CommentRepository commentRepository, CommentMapper commentMapper, CommentService commentService, AuthenticationHelper authenticationHelper) {
        this.postService = postService;
        this.viewRepository = viewRepository;
        this.commentRepository = commentRepository;
    this.commentMapper = commentMapper;
    this.commentService = commentService;
    this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/{id}")
    public String singlePostView(Model model, @PathVariable int id, HttpSession session) {
        try{
           User user = authenticationHelper.tryGetUser(session);
            postService.tryViewingPost(id, user.getId());
        }catch (AuthenticationFailureException ignored){}
        try {
            Post post = postService.getById(id);
            model.addAttribute("post", post);
            model.addAttribute("commentDto", new CommentDto());
            model.addAttribute("viewCount", viewRepository.getViewsCountOnPost(id));
            model.addAttribute("post_comments", commentRepository.getByPostId(id));
            return "PostView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }

    }
    @PostMapping("/{id}")
    public String commentOnPost(@Valid @ModelAttribute("comment") CommentDto commentDto,
                                BindingResult bindingResult,
                                Model model, HttpSession session,
                                @PathVariable int id) {
    User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }
        try {
            Comment comment = commentMapper.fromDto(commentDto, user, id);
            commentService.create(comment, user);
            return "redirect:/posts/" + id;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }
//    @GetMapping()
//    public String PostsView(@ModelAttribute("filterOptions") FilterDto filterDto, Model model) {
//        FilterOptions filterOptions = new FilterOptions(
//                filterDto.getName(),
//                filterDto.getMinAbv(),
//                filterDto.getMaxAbv(),
//                filterDto.getStyleId(),
//                filterDto.getSortBy(),
//                filterDto.getSortOrder());
//        List<Post> posts = postService.get(filterOptions);
//        model.addAttribute("filterOptions", filterDto);
//        model.addAttribute("posts", posts);
//        return "PostsView";
//    }
}
