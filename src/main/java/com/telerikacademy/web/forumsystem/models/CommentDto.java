package com.telerikacademy.web.forumsystem.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CommentDto {

    @NotNull(message = "Content can't be empty")
    @Size(min = 1, max = 500, message = "Content should be between 1 and 500 symbols")
    private String content;
    private LocalDate timeStamp;

    public CommentDto() {
        this.timeStamp= LocalDate.now();
    }

    public CommentDto(String content) {
        this.content = content;
        this.timeStamp = LocalDate.now();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public LocalDate getTimeStamp() {
        return timeStamp;
    }


    public void setTimeStamp(LocalDate timeStamp) {
        this.timeStamp = timeStamp;
    }
}
