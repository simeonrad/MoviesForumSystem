package com.telerikacademy.web.forumsystem.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "post_likes")
public class Like {
   @Id
   @Column(name = "post_id")
    private int postId;
   @Id
   @Column(name = "user_id")
    private int userId;

    public Like() {
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
}
