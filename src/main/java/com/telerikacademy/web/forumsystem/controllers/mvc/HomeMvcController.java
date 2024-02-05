package com.telerikacademy.web.forumsystem.controllers.mvc;

import com.telerikacademy.web.forumsystem.models.Post;
import com.telerikacademy.web.forumsystem.services.PostService;
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

    @Autowired
    public HomeMvcController(PostService postService) {
        this.postService = postService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping
    public String showHomePage(Model model) {
        List<Post> mostRecentPosts = postService.getMostRecentPosts(10);
        List<Post> mostCommentedPosts = postService.getMostCommentedPosts(10);

        model.addAttribute("mostRecentPosts", mostRecentPosts);
        model.addAttribute("mostCommentedPosts", mostCommentedPosts);

        return "index";
    }
}
