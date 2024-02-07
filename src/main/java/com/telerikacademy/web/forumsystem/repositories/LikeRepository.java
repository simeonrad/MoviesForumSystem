package com.telerikacademy.web.forumsystem.repositories;

import com.telerikacademy.web.forumsystem.models.Like;

public interface LikeRepository {
    Like getByPostAndUserId(int postId, int userId);
    void likeAPost(int postId, int userId);
    void unlikeAPost(int postId, int userId);
    int getLikesCountOnPost(int postId);
}
