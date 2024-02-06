package com.telerikacademy.web.forumsystem.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_views")
public class PostView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "view_id")
    private int viewId;
    @Column(name = "post_id")
    private int postId;
    @Column(name = "user_id")
    private int userId;
    @Column(name = "time_stamp")
    private LocalDateTime timestamp;

    public PostView() {
        this.timestamp = LocalDateTime.now();
    }

    public PostView(int postId, int userId) {
        this.postId = postId;
        this.userId = userId;
        this.timestamp = LocalDateTime.now();
    }

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
