package com.telerikacademy.web.forumsystem.controllers.mvc;

import com.telerikacademy.web.forumsystem.models.Post;
import com.telerikacademy.web.forumsystem.models.User;
import com.telerikacademy.web.forumsystem.repositories.PostRepository;
import com.telerikacademy.web.forumsystem.repositories.UserRepository;
import com.telerikacademy.web.forumsystem.services.PostService;
import com.telerikacademy.web.forumsystem.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final PostService postService;

    private final PostRepository postRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public AdminController(PostService postService, PostRepository postRepository, UserService userService, UserRepository userRepository) {
        this.postService = postService;
        this.postRepository = postRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping()
    public String adminDashboard(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null && currentUser.isAdmin()) {
            List<Post> allPosts = postService.getAll();
            List<User> allUsers = userService.getAll();

            model.addAttribute("allPosts", allPosts);
            model.addAttribute("allUsers", allUsers);

            return "admin/dashboard";
        } else {
            return "redirect:/auth/login";
        }
    }


    @PostMapping("/delete-post/{id}")
    public String deletePost(@PathVariable("id") int postId, HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        Post currentPost = postService.getById(postId);
        if (currentUser != null && currentUser.isAdmin()) {
            try {
                postRepository.delete(currentPost);
                model.addAttribute("message", "Post successfully deleted.");
            } catch (Exception e) {
                model.addAttribute("error", "Error deleting post.");
            }
            return "redirect:/admin"; // Return to the admin dashboard
        } else {
            return "redirect:/auth/login";
        }
    }

    @PostMapping("/toggle-block/{id}")
    public String toggleBlockUser(@PathVariable("id") int userId, HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null && currentUser.isAdmin()) {
            User userToToggle = userRepository.getById(userId);
            try {
                if (userToToggle.getIsBlocked()) {
                    userService.unblockUser(userToToggle.getUsername(), currentUser);
                    model.addAttribute("message", "User successfully unblocked.");
                } else {
                    userService.blockUser(userToToggle.getUsername(), currentUser);
                    model.addAttribute("message", "User successfully blocked.");
                }
            } catch (Exception e) {
                model.addAttribute("error", "Error toggling user block status.");
            }
            return "redirect:/admin";
        } else {
            return "redirect:/auth/login";
        }
    }

    @PostMapping("/toggle-admin/{id}")
    public String toggleAdminStatus(@PathVariable("id") int userId, HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null && currentUser.isAdmin()) {
            User userToToggle = userRepository.getById(userId);
            try {
                if (userToToggle.isAdmin()) {
                    userService.unmakeAdmin(userToToggle.getUsername(), currentUser);
                    model.addAttribute("message", "Admin privileges successfully removed.");
                } else {
                    userService.makeAdmin(userToToggle.getUsername());
                    model.addAttribute("message", "Admin privileges successfully granted.");
                }
            } catch (Exception e) {
                model.addAttribute("error", "Error toggling admin status.");
            }
            return "redirect:/admin";
        } else {
            return "redirect:/auth/login";
        }
    }




}
