package com.telerikacademy.web.forumsystem.repositories;

import com.telerikacademy.web.forumsystem.exceptions.EntityNotFoundException;
import com.telerikacademy.web.forumsystem.models.Post;
import com.telerikacademy.web.forumsystem.models.PostsFilterOptions;
import com.telerikacademy.web.forumsystem.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Page<Post> findUserPosts(User currentUser, Pageable pageable) {
        String query = "SELECT p FROM Post p WHERE p.author = :currentUser";
        List<Post> posts = entityManager.createQuery(query, Post.class)
                .setParameter("currentUser", currentUser)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        String countQuery = "SELECT COUNT(p) FROM Post p WHERE p.author = :currentUser";
        long totalPostsCount = entityManager.createQuery(countQuery, Long.class)
                .setParameter("currentUser", currentUser)
                .getSingleResult();

        return new PageImpl<>(posts, pageable, totalPostsCount);
    }

    @Override
    public List<Post> get(PostsFilterOptions filterOptions) {
        try (Session session = sessionFactory.openSession()) {
            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            filterOptions.getTitle().ifPresent(value -> {
                if (!value.isBlank()) {
                    filters.add("p.title like :title");
                    params.put("title", "%" + value + "%");
                }
            });

            filterOptions.getContent().ifPresent(value -> {
                if (!value.isBlank()) {
                    filters.add("p.content like :content");
                    params.put("content", "%" + value + "%");
                }
            });

            filterOptions.getUserCreator().ifPresent(value -> {
                if (!value.isBlank()) {
                    filters.add("a.username like :username");
                    params.put("username", "%" + value + "%");
                }
            });

            StringBuilder queryString = new StringBuilder("select p from Post p join p.author a");

            if (!filters.isEmpty()) {
                queryString.append(" where ").append(String.join(" and ", filters));
            }
            queryString.append(generateOrderBy(filterOptions));

            Query<Post> query = session.createQuery(queryString.toString(), Post.class);
            query.setProperties(params);
            return query.list();
        }
    }


    private String generateOrderBy(PostsFilterOptions filterOptions) {
        if (filterOptions.getSortBy().isEmpty()) {
            return "";
        }

        String orderBy = switch (filterOptions.getSortBy().get()) {
            case "title" -> "title";
            case "userCreator" -> "user_id";
            case "timeStamp" -> "timeStamp";
            default -> "";
        };

        if (orderBy.isEmpty()) {
            return "";
        }

        orderBy = String.format(" order by %s", orderBy);

        if (filterOptions.getSortOrder().isPresent() && filterOptions.getSortOrder().get().equalsIgnoreCase("desc")) {
            orderBy = String.format("%s desc", orderBy);
        }

        return orderBy;
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
