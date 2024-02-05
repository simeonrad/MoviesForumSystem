package com.telerikacademy.web.forumsystem.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public class PostDto {
    private int id;
    private int authorId;

    @NotNull(message = "Title can't be empty")
    @Size(min = 16, max = 64, message = "Title should be between 16 and 64 symbols")
    private String title;

    @NotNull(message = "Content can't be empty")
    @Size(min = 32, max = 8192, message = "Content should be between 32 and 8192 symbols")
    private String content;
    private int likes;
    private LocalDate timeStamp;
    private Set<Tag> tag;

    public PostDto() {
        timeStamp = LocalDate.now();
    }

    public int getId() {
        return id;
    }

    public int getAuthorId() {
        return authorId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getLikes() {
        return likes;
    }

    public LocalDate getTimeStamp() {
        return timeStamp;
    }
    public void setId(int id) {
        this.id = id;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setTimeStamp(LocalDate timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Set<Tag> getTag() {
        return tag;
    }

    public void setTag(Set<Tag> tag) {
        this.tag = tag;
    }
}
