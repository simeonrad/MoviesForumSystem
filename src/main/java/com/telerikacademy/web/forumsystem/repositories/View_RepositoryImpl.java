package com.telerikacademy.web.forumsystem.repositories;

import com.telerikacademy.web.forumsystem.exceptions.EntityNotFoundException;
import com.telerikacademy.web.forumsystem.models.PostView;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class View_RepositoryImpl implements View_Repository {
    private final SessionFactory sessionFactory;

    @Autowired
    public View_RepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public PostView getMostRecentViewByPostAndUserId(int postId, int userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<PostView> query = session.createQuery("from PostView where postId = :post_id AND userId = :user_id order by timestamp desc", PostView.class);
            query.setParameter("post_id", postId);
            query.setParameter("user_id", userId);
            query.setMaxResults(1);

            List<PostView> results = query.list();

            if (results.isEmpty()) {
                throw new EntityNotFoundException("Post view", postId);
            }

            return results.get(0);
        }
    }

    @Override
    public void viewAPost(int postId, int userId) {
        Transaction transaction;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            PostView postView = new PostView();
            postView.setPostId(postId);
            postView.setUserId(userId);

            session.persist(postView);

            transaction.commit();
        }
    }

    @Override
    public int getViewsCountOnPost(int postId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("select count(*) from PostView where postId = :post_id", Long.class);
            query.setParameter("post_id", postId);
            return query.uniqueResult().intValue();
        }
    }
}
