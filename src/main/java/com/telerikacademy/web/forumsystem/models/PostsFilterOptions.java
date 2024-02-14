package com.telerikacademy.web.forumsystem.models;

import java.util.Optional;

public class PostsFilterOptions {
    private Optional<String> title;
    private Optional<String> content;
    private Optional<String> userCreator;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;

    public PostsFilterOptions(String title, String content, String userCreator, String sortBy, String sortOrder) {
        this.title = Optional.ofNullable(title);
        this.content = Optional.ofNullable(content);
        this.userCreator = Optional.ofNullable(userCreator);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }

    public Optional<String> getTitle() {
        return title;
    }

    public Optional<String> getContent() {
        return content;
    }

    public Optional<String> getUserCreator() {
        return userCreator;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }
}
