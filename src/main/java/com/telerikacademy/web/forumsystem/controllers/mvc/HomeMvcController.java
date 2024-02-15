package com.telerikacademy.web.forumsystem.controllers.mvc;

import com.telerikacademy.web.forumsystem.models.FilterDto;
import com.telerikacademy.web.forumsystem.models.FilterOptions;
import com.telerikacademy.web.forumsystem.models.Post;
import com.telerikacademy.web.forumsystem.models.User;
import com.telerikacademy.web.forumsystem.services.CommentService;
import com.telerikacademy.web.forumsystem.services.PostService;
import com.telerikacademy.web.forumsystem.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeMvcController {

    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;

    @Autowired
    public HomeMvcController(PostService postService, UserService userService, CommentService commentService) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @ModelAttribute("isAdmin")
    public boolean populateIsAdmin(HttpSession session) {
        boolean isAdmin = false;
        if (populateIsAuthenticated(session)) {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser.isAdmin()) {
                isAdmin = true;
            }
        }
        return isAdmin;
    }

    @GetMapping
    public String showHomePage(Model model, HttpSession session) {
        List<Post> mostRecentPosts = postService.getMostRecentPosts(10);
        List<Post> mostCommentedPosts = postService.getMostCommentedPosts(10);
        int totalUsers = userService.getAllNotDeleted().size();
        int totalPosts = postService.getAll().size();
        int totalComments = commentService.getAll().size();
        model.addAttribute("mostRecentPosts", mostRecentPosts);
        model.addAttribute("mostCommentedPosts", mostCommentedPosts);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalPosts", totalPosts);
        model.addAttribute("totalComments", totalComments);
        return "index";
    }

    @GetMapping("/search-user")
    public String showSearchUserPage(@ModelAttribute("filterOptions") FilterDto filterDto, Model model, @RequestParam(defaultValue = "0", name = "page") int page,
                                     @RequestParam(defaultValue = "5", name = "size") int size, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            Page<User> users = userService.get(new FilterOptions(
                    filterDto.getUsername(),
                    filterDto.getEmail(),
                    filterDto.getFirstName(),
                    filterDto.getSortBy(),
                    filterDto.getSortOrder()), page, size);
            model.addAttribute("filterOptions", filterDto);
            model.addAttribute("users", users);
            return "searchUserView";
        } else {
            return "redirect:/auth/login";
        }
    }
}
