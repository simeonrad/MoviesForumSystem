package com.telerikacademy.web.forumsystem.repositories;

import com.telerikacademy.web.forumsystem.exceptions.EntityNotFoundException;
import com.telerikacademy.web.forumsystem.exceptions.InvalidParameterException;
import com.telerikacademy.web.forumsystem.models.FilterOptions;
import com.telerikacademy.web.forumsystem.models.User;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final SessionFactory sessionFactory;

    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(user);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(user);
            session.getTransaction().commit();
        }

    }

    @Override
    public List<User> get(FilterOptions filterOptions) {
        try (Session session = sessionFactory.openSession()) {
            List<String> filters = new ArrayList<>();
            List<String> params = new ArrayList<>();

            filterOptions.getEmail().ifPresent(value -> {
                filters.add("email = :email");
                params.add("email");
                params.add(value);
            });

            filterOptions.getUsername().ifPresent(value -> {
                filters.add("username = :username");
                params.add("username");
                params.add(value);
            });

            filterOptions.getFirstName().ifPresent(value -> {
                filters.add("firstName = :firstName");
                params.add("firstName");
                params.add(value);
            });


            StringBuilder queryString = new StringBuilder("from User");
            if (!filters.isEmpty()) {
                queryString
                        .append(" where ")
                        .append(String.join(" and ", filters));
            } else {
                throw new InvalidParameterException("Invalid parameter. Supported values are: 'username', 'firstName', 'email'");
            }
            queryString.append(generateOrderBy(filterOptions));

            Query<User> query = session.createQuery(queryString.toString(), User.class);
            query.setParameter(params.get(0), params.get(1));
            if (query.list().isEmpty()) {
                throw new EntityNotFoundException("User", params.get(0), params.get(1));
            }
            return query.list();
        }
    }

    private String generateOrderBy(FilterOptions filterOptions) {
        if (filterOptions.getSortBy().isEmpty()) {
            return "";
        }

        String orderBy = switch (filterOptions.getSortBy().get()) {
            case "name" -> "name";
            case "firstName" -> "firstName";
            case "username" -> "username";
            default -> "";
        };

        orderBy = String.format(" order by %s", orderBy);

        if (filterOptions.getSortOrder().isPresent() && filterOptions.getSortOrder().get().equalsIgnoreCase("desc")) {
            orderBy = String.format("%s desc", orderBy);
        }

        return orderBy;
    }

    @Override
    public User getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user == null) {
                throw new EntityNotFoundException("User", id);
            }
            return user;
        }
    }

    @Override
    public List<User> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User", User.class);
            return query.list();
        }
    }

    @Override
    public User getByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where username = :username", User.class);
            query.setParameter("username", username);
            List<User> result = query.list();
            if (result.isEmpty()) {
                throw new EntityNotFoundException("User", "username", username);
            }
            return result.get(0);
        }
    }

    @Override
    public User getByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where email = :email", User.class);
            query.setParameter("email", email);
            List<User> result = query.list();
            if (result.isEmpty()) {
                throw new EntityNotFoundException("Email", "name", email);
            }
            return result.get(0);
        }
    }

    @Override
    public boolean updateEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where email = :email", User.class);
            query.setParameter("email", email);
            List<User> result = query.list();
            if (result.size() == 1 || result.isEmpty()) {
                return false;
            } else {
                return true;
            }
        }
    }
}
