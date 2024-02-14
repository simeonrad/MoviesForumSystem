package com.telerikacademy.web.forumsystem.controllers.mvc;

import com.telerikacademy.web.forumsystem.models.FilterDto;
import com.telerikacademy.web.forumsystem.models.FilterOptions;
import com.telerikacademy.web.forumsystem.models.Post;
import com.telerikacademy.web.forumsystem.models.User;
import com.telerikacademy.web.forumsystem.services.PostService;
import com.telerikacademy.web.forumsystem.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeMvcController {

    private final PostService postService;
    private final UserService userService;

    @Autowired
    public HomeMvcController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
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

        model.addAttribute("mostRecentPosts", mostRecentPosts);
        model.addAttribute("mostCommentedPosts", mostCommentedPosts);

        return "index";
    }

    @GetMapping("/search-user")
    public String showSearchUserPage(@ModelAttribute("filterOptions") FilterDto filterDto, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            List<User> users = userService.get(new FilterOptions(
                    filterDto.getUsername(),
                    filterDto.getEmail(),
                    filterDto.getFirstName(),
                    filterDto.getSortBy(),
                    filterDto.getSortOrder()));
            model.addAttribute("filterOptions", filterDto);
            model.addAttribute("users", users);
            return "searchUserView";
        } else {
            return "redirect:/auth/login";
        }
    }
}
