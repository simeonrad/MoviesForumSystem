package com.telerikacademy.web.forumsystem.controllers.mvc;


import com.telerikacademy.web.forumsystem.models.*;
import com.telerikacademy.web.forumsystem.services.CommentService;
import com.telerikacademy.web.forumsystem.services.ImageStorageService;
import com.telerikacademy.web.forumsystem.services.PostService;
import com.telerikacademy.web.forumsystem.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;
    private final ImageStorageService imageStorageService;

    @Autowired
    public ProfileController(UserService userService, ImageStorageService imageStorageService, ImageStorageService imageStorageService1, PostService postService, CommentService commentService) {
        this.userService = userService;
        this.imageStorageService = imageStorageService;
        this.postService = postService;
        this.commentService = commentService;
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

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping()
    public String showProfile(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        boolean isAdmin = currentUser.isAdmin();
        model.addAttribute("isAdmin", isAdmin);

        UserProfileDto namesDto = new UserProfileDto();
        namesDto.setFirstName(currentUser.getFirstName());
        namesDto.setLastName(currentUser.getLastName());

        UserEmailUpdateDto emailDto = new UserEmailUpdateDto();
        emailDto.setEmail(currentUser.getEmail());

        UserPasswordUpdateDto passwordDto = new UserPasswordUpdateDto();

        model.addAttribute("namesDto", namesDto);
        model.addAttribute("emailDto", emailDto);
        model.addAttribute("passwordDto", passwordDto);

        return "profile";
    }

    @PostMapping("/update-password")
    public String updatePassword(@Valid @ModelAttribute("passwordDto") UserPasswordUpdateDto passwordDto,
                                 BindingResult bindingResult, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser.getUsername() == null) {
            return "redirect:/login";
        }


        if (!currentUser.getPassword().equals(passwordDto.getCurrentPassword())) {
            bindingResult.rejectValue("currentPassword", "error.passwordDto", "Invalid current password.");
        }

        if (passwordDto.getNewPassword().isEmpty()) {
            bindingResult.rejectValue("newPassword", "error.passwordDto", "New password cannot be empty.");
        } else {
            if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmNewPassword())) {
                bindingResult.rejectValue("confirmNewPassword", "error.passwordDto", "New password and confirm password do not match.");
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("namesDto", new UserProfileDto());
            model.addAttribute("emailDto", new UserEmailUpdateDto());
            model.addAttribute("passwordDto", passwordDto);
            return "profile";
        }

        currentUser.setPassword(passwordDto.getNewPassword());
        userService.update(currentUser);

        model.addAttribute("successMessage", "Password updated successfully.");
        return "redirect:/profile";
    }


    @PostMapping("/update-names")
    public String updateNames(@Valid @ModelAttribute("namesDto") UserProfileDto namesDto,
                              BindingResult bindingResult, HttpSession session, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("emailDto", new UserEmailUpdateDto());
            model.addAttribute("passwordDto", new UserPasswordUpdateDto());
            model.addAttribute("namesDto", namesDto);
            return "profile";
        }

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser.getUsername() == null) {
            return "redirect:/login";
        }

        if (currentUser == null) {
            model.addAttribute("errorMessage", "User not found.");
            return "profile";
        }

        currentUser.setFirstName(namesDto.getFirstName());
        currentUser.setLastName(namesDto.getLastName());
        userService.update(currentUser);

        model.addAttribute("successMessage", "Names updated successfully.");
        return "redirect:/profile";
    }


    @PostMapping("/update-email")
    public String updateEmail(@Valid @ModelAttribute("emailDto") UserEmailUpdateDto emailDto,
                              BindingResult bindingResult, HttpSession session, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("namesDto", new UserProfileDto());
            model.addAttribute("passwordDto", new UserPasswordUpdateDto());
            model.addAttribute("emailDto", emailDto);
            return "profile";
        }

        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            model.addAttribute("errorMessage", "User not found.");
            return "profile";
        }

        currentUser.setEmail(emailDto.getEmail());
        userService.update(currentUser);

        model.addAttribute("successMessage", "Email updated successfully.");
        return "redirect:/profile";
    }

    @GetMapping("/delete-confirm")
    public String showDeleteConfirmation(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser.getUsername() == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("firstName", currentUser.getFirstName()); // Add first name to model
        return "delete-confirm";
    }

    @PostMapping("/delete")
    public String deleteProfile(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser.getUsername() != null) {
            userService.delete(currentUser);
            session.invalidate();
            return "redirect:/auth/login";
        }

        return "redirect:/auth/login";
    }

    @PostMapping("/upload-image")
    public String uploadProfileImage(@ModelAttribute ProfileImageForm form, HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/auth/login";
        }

        try {
            User currentUser = (User) session.getAttribute("currentUser");
            String imageUrl = imageStorageService.saveImage(form.getImage());
            currentUser.setProfilePhotoUrl(imageUrl);
            userService.addProfilePhoto(imageUrl, currentUser);
            model.addAttribute("message", "Profile image updated successfully.");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to upload image.");
        }

        return "redirect:/profile";
    }


}
