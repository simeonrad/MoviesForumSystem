package com.telerikacademy.web.forumsystem.repositories;

import com.telerikacademy.web.forumsystem.exceptions.EntityNotFoundException;
import com.telerikacademy.web.forumsystem.models.Like;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class LikeRepositoryImpl implements LikeRepository {
    private final SessionFactory sessionFactory;

    public LikeRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    @Override
    public Like getByPostAndUserId(int postId, int userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Like> query = session.createQuery("from Like where postId = :post_id AND userId =:user_id ", Like.class);
            query.setParameter("post_id", postId);
            query.setParameter("user_id", userId);
            if (query.list().isEmpty()) {
                throw new EntityNotFoundException("Like", postId);
            }
            return query.list().get(0);
        }
    }
    @Override
    public void likeAPost(int postId, int userId) {
        try (Session session = sessionFactory.openSession()) {
            Like like = new Like();
            like.setPostId(postId);
            like.setUserId(userId);
            session.persist(like);
            session.getTransaction().commit();
        }
    }
    @Override
    public void unlikeAPost(int postId, int userId) {
        try (Session session = sessionFactory.openSession()) {
            Like like = new Like();
            like.setPostId(postId);
            like.setUserId(userId);
            session.remove(like);
            session.getTransaction().commit();
        }
    }
}
