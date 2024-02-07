package com.telerikacademy.web.forumsystem.controllers.mvc;


import com.telerikacademy.web.forumsystem.models.User;
import com.telerikacademy.web.forumsystem.models.UserEmailUpdateDto;
import com.telerikacademy.web.forumsystem.models.UserPasswordUpdateDto;
import com.telerikacademy.web.forumsystem.models.UserProfileDto;
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
    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String showProfile(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser.getUsername() == null) {
            return "redirect:/login";
        }

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


}