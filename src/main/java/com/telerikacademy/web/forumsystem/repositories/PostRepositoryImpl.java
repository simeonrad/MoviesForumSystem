package com.telerikacademy.web.forumsystem.repositories;

import com.telerikacademy.web.forumsystem.exceptions.EntityNotFoundException;
import com.telerikacademy.web.forumsystem.models.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostRepositoryImpl implements PostRepository {

    public static final String NO_POSTS_FOUND = "No posts found";
    private final SessionFactory sessionFactory;

    @PersistenceContext
    private EntityManager entityManager;

    public PostRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(Post post) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(post);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Post post) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(post);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Post post) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(post);
            session.getTransaction().commit();
        }
    }

    @Override
    public Post getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Post post = session.get(Post.class, id);
            if (post == null) {
                throw new EntityNotFoundException("Post", "id", Integer.toString(id));
            }
            Hibernate.initialize(post.getTags());
            Hibernate.initialize(post.getLikedByUsers());
            return post;
        }
    }


    @Override
    public List<Post> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery("from Post", Post.class);
            if (query.list().isEmpty()){
                throw new EntityNotFoundException(NO_POSTS_FOUND);
            }
            return query.list();
        }
    }

    @Override
    public List<Post> findMostRecentPosts(int limit) {

        List<Post> query = entityManager.createQuery("SELECT p FROM Post p ORDER BY p.timeStamp DESC", Post.class)
                .setMaxResults(limit)
                .getResultList();
        if (query.isEmpty()){
            throw new EntityNotFoundException(NO_POSTS_FOUND);
        }
        return query;
    }

    @Override
    public List<Post> findMostCommentedPosts(int limit) {
        List<Post> query = entityManager.createQuery("SELECT p FROM Post p LEFT JOIN p.comments c GROUP BY p ORDER BY COUNT(c) DESC", Post.class)
                .setMaxResults(limit)
                .getResultList();
        if (query.isEmpty()){
            throw new EntityNotFoundException(NO_POSTS_FOUND);
        }
        return query;
    }
}
