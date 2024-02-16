package com.telerikacademy.web.forumsystem.repositories;

import com.telerikacademy.web.forumsystem.exceptions.EntityNotFoundException;
import com.telerikacademy.web.forumsystem.models.Comment;
import com.telerikacademy.web.forumsystem.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentRepositoryImpl implements CommentRepository {
    private final SessionFactory sessionFactory;
    @PersistenceContext
    private EntityManager entityManager;

    public CommentRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(Comment comment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(comment);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Comment comment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(comment);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Comment comment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(comment);
            session.getTransaction().commit();
        }
    }

    @Override
    public Comment getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Comment comment = session.get(Comment.class, id);
            if (comment == null) {
                throw new EntityNotFoundException("Post", id);
            }
            return comment;
        }
    }

    @Override
    public List<Comment> getByPostId(int id) {
        try (Session session = sessionFactory.openSession()) {
            Query<Comment> query = session.createQuery("from Comment where post.id = :id", Comment.class);
            query.setParameter("id", id);
            return query.list();
        }
    }

    @Override
    public List<Comment> getByCommentId(int id) {
        try (Session session = sessionFactory.openSession()) {
            Query<Comment> query = session.createQuery("from Comment where repliedTo.id = :comment_id", Comment.class);
            query.setParameter("comment_id", id);
            return query.list();
        }
    }

    @Override
    public List<Comment> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Comment> query = session.createQuery("from Comment", Comment.class);
            return query.list();
        }
    }

    @Override
    public Page<Comment> getUserComments(User currentUser, Pageable pageable) {
        String fetchQuery = "SELECT c FROM Comment c WHERE c.author = :currentUser ORDER BY c.timeStamp DESC";
        List<Comment> comments = entityManager.createQuery(fetchQuery, Comment.class)
                .setParameter("currentUser", currentUser)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        String countQuery = "SELECT COUNT(c) FROM Comment c WHERE c.author = :currentUser";
        long totalCommentsCount = entityManager.createQuery(countQuery, Long.class)
                .setParameter("currentUser", currentUser)
                .getSingleResult();

        return new PageImpl<>(comments, pageable, totalCommentsCount);
    }

}
