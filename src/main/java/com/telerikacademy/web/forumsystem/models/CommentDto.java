package com.telerikacademy.web.forumsystem.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CommentDto {

    private int id;

    @NotNull(message = "User ID can't be null")
    private int userId;

    @NotNull(message = "Post ID can't be null")
    private int postId;

    @NotNull(message = "Content can't be empty")
    @Size(min = 1, max = 500, message = "Content should be between 1 and 500 symbols")
    private String content;

    private LocalDate timeStamp;

    public CommentDto() {
    }

    public CommentDto(int id, int userId, int postId, String content, LocalDate timeStamp) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.content = content;
        this.timeStamp = timeStamp;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getPostId() {
        return postId;
    }

    public String getContent() {
        return content;
    }

    public LocalDate getTimeStamp() {
        return timeStamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimeStamp(LocalDate timeStamp) {
        this.timeStamp = timeStamp;
    }
    public void create(Comment comment) {

    }
}
