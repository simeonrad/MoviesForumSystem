package com.telerikacademy.web.forumsystem.repositories;

import com.telerikacademy.web.forumsystem.models.PostView;

public interface View_Repository {
    PostView getMostRecentViewByPostAndUserId(int postId, int userId);

    void viewAPost(int postId, int userId);
    int getViewsCountOnPost(int postId);
}
