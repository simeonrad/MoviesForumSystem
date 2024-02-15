package com.telerikacademy.web.forumsystem.repositories;

import com.telerikacademy.web.forumsystem.exceptions.EntityNotFoundException;
import com.telerikacademy.web.forumsystem.models.FilterOptions;
import com.telerikacademy.web.forumsystem.models.PhoneNumber;
import com.telerikacademy.web.forumsystem.models.User;

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
    public void addPhone(PhoneNumber phoneNumber) {
        User user = phoneNumber.getUser();
        PhoneNumber existingPhoneNumber = findPhoneNumberByUser(user);
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            if (existingPhoneNumber != null) {
                existingPhoneNumber.setPhoneNumber(phoneNumber.getPhoneNumber());
                session.merge(existingPhoneNumber);
            } else {
                session.persist(phoneNumber);
            }
            session.getTransaction().commit();
        }
    }

    private PhoneNumber findPhoneNumberByUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            String queryStr = "FROM PhoneNumber WHERE user = :user";
            Query<PhoneNumber> query = session.createQuery(queryStr, PhoneNumber.class);
            query.setParameter("user", user);
            return query.uniqueResult();
        }
    }

    @Override
    public List<User> get(FilterOptions filterOptions) {
        try (Session session = sessionFactory.openSession()) {
            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            filterOptions.getUsername().ifPresent(value -> {
                if (!value.isBlank()) {
                    filters.add("username like :username");
                    params.put("username", String.format("%%%s%%", value));
                }
            });

            filterOptions.getEmail().ifPresent(value -> {
                if (!value.isBlank()) {
                    filters.add("email = :email");
                    params.put("email", String.format("%s", value));
                }
            });

            filterOptions.getFirstName().ifPresent(value -> {
                if (!value.isBlank()) {
                filters.add("firstName like :firstName");
                params.put("firstName", String.format("%%%s%%", value));
                }
            });

            StringBuilder queryString = new StringBuilder("from User");
            if (!filters.isEmpty()) {
                queryString
                        .append(" where ")
                        .append(String.join(" and ", filters));
            }
            queryString.append(generateOrderBy(filterOptions));

            Query<User> query = session.createQuery(queryString.toString(), User.class);
            query.setProperties(params);
            return query.list();
        }
    }

    @Override
    public Page<User> get(FilterOptions filterOptions, Pageable pageable) {
        try (Session session = sessionFactory.openSession()) {
            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            filterOptions.getUsername().ifPresent(value -> {
                if (!value.isBlank()) {
                    filters.add("u.username like :username");
                    params.put("username", "%" + value + "%");
                }
            });

            filterOptions.getEmail().ifPresent(value -> {
                if (!value.isBlank()) {
                    filters.add("u.email = :email");
                    params.put("email", value);
                }
            });

            filterOptions.getFirstName().ifPresent(value -> {
                if (!value.isBlank()) {
                    filters.add("u.firstName like :firstName");
                    params.put("firstName", "%" + value + "%");
                }
            });

            StringBuilder queryString = new StringBuilder("from User u");
            StringBuilder countQueryString = new StringBuilder("select count(u) from User u");

            if (!filters.isEmpty()) {
                String whereClause = " where " + String.join(" and ", filters);
                queryString.append(whereClause);
                countQueryString.append(whereClause);
            }

            queryString.append(generateOrderBy(filterOptions));

            Query<Long> countQuery = session.createQuery(countQueryString.toString(), Long.class);
            countQuery.setProperties(params);
            long total = countQuery.uniqueResult();

            Query<User> query = session.createQuery(queryString.toString(), User.class);
            query.setProperties(params);
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());

            List<User> users = query.list();
            return new PageImpl<>(users, pageable, total);
        }
    }


    private String generateOrderBy(FilterOptions filterOptions) {
        if (filterOptions.getSortBy().isEmpty()) {
            return "";
        }

        String orderBy = switch (filterOptions.getSortBy().get()) {
            case "email" -> "email";
            case "firstName" -> "firstName";
            case "username" -> "username";
            default -> "id";
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
    public List<User> getAllNotDeleted() {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User u where u.isDeleted = false", User.class);
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
            if (result.isEmpty() || result.size() == 1) {
                return false;
            } else {
                return true;
            }
        }
    }
}
