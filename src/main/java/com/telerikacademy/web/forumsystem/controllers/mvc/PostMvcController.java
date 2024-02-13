package com.telerikacademy.web.forumsystem.controllers.mvc;

import com.telerikacademy.web.forumsystem.exceptions.EntityNotFoundException;
import com.telerikacademy.web.forumsystem.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.forumsystem.helpers.AuthenticationHelper;
import com.telerikacademy.web.forumsystem.helpers.PostMapper;
import com.telerikacademy.web.forumsystem.models.Post;
import com.telerikacademy.web.forumsystem.models.PostDto;
import com.telerikacademy.web.forumsystem.models.Tag;
import com.telerikacademy.web.forumsystem.models.User;
import com.telerikacademy.web.forumsystem.services.PostService;
import com.telerikacademy.web.forumsystem.services.TagService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/posts")
public class PostMvcController {

    private final AuthenticationHelper authenticationHelper;
    private final PostService postService;
    private final PostMapper postMapper;
    private final TagService tagService;

    @Autowired
    public PostMvcController(AuthenticationHelper authenticationHelper, PostService postService, PostMapper postMapper, TagService tagService) {
        this.authenticationHelper = authenticationHelper;
        this.postService = postService;
        this.postMapper = postMapper;
        this.tagService = tagService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping("/new")
    public String showCreatePostForm(Model model) {
        model.addAttribute("postDto", new PostDto());
        return "createPost";
    }

    @PostMapping()
    public String createPost(@ModelAttribute PostDto postDto, Model model, HttpSession session) {
        try {
            User currentUser = authenticationHelper.tryGetUser(session);
            Post post = postMapper.fromDto(postDto);
            postService.create(post, currentUser);
            return "redirect:/posts";
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        }
    }


    @GetMapping("/edit/{postId}")
    public String showUpdatePostForm(@PathVariable("postId") int postId, Model model, HttpSession session) {
        try {
            User currentUser = authenticationHelper.tryGetUser(session);
            Post post = postService.getById(postId);

            model.addAttribute("postDto", postMapper.toDto(post));
            if (currentUser.isAdmin() && post.getAuthor().equals(currentUser)) {
                return "updatePost";
            } else if (currentUser.isAdmin() && !post.getAuthor().equals(currentUser)) {
                return "adminUpdatePost";
            } else if (post.getAuthor().equals(currentUser) && !currentUser.isAdmin()) {
                return "updatePost";
            } else {
                return "redirect:/posts";
            }

        } catch (UnauthorizedOperationException | EntityNotFoundException e) {
            return "redirect:/posts";
        }
    }

    @PostMapping("/update/{postId}")
    public String updatePost(@PathVariable("postId") int postId,
                             @ModelAttribute("postDto") PostDto postDto,
                             @RequestParam(required = false) String tags,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        try {
            User currentUser = authenticationHelper.tryGetUser(session);
            Post existingPost = postService.getById(postId);

            if (!existingPost.getAuthor().equals(currentUser) && !currentUser.isAdmin()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized to edit this post.");
                return "redirect:/posts";
            }

            postMapper.updateFromDto(postDto, existingPost);

            Set<Tag> processTags = processTags(tags);
            existingPost.setTags(processTags);

            postService.update(existingPost, currentUser, processTags);

            redirectAttributes.addFlashAttribute("successMessage", "Post updated successfully!");
            return "redirect:/posts";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating post: " + e.getMessage());
            return "redirect:/posts/edit/" + postId;
        }
    }

    private Set<Tag> processTags(String tagInput) {
        Set<Tag> tags = new HashSet<>();
        if (tagInput != null && !tagInput.isEmpty()) {
            String[] tagNames = tagInput.split(",\\s*");
            for (String tagName : tagNames) {
                Tag tag = tagService.getByName(tagName.trim().toLowerCase());
                if (tag == null) {
                    tagService.create(tagName.trim().toLowerCase());
                    tag = tagService.getByName(tagName.trim().toLowerCase());
                }
                tags.add(tag);
            }
        }
        return tags;
    }


}
