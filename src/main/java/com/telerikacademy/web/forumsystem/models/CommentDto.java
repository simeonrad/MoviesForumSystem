package com.telerikacademy.web.forumsystem.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CommentDto {

    @NotNull(message = "Content can't be empty")
    @Size(min = 1, max = 500, message = "Content should be between 1 and 500 symbols")
    private String content;
    private LocalDateTime timeStamp;

    public CommentDto() {
        this.timeStamp= LocalDateTime.now();
    }

    public CommentDto(String content) {
        this.content = content;
        this.timeStamp = LocalDateTime.now();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }


    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }
}
