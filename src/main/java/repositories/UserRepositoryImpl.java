package repositories;

import exceptions.EntityNotFoundException;
import models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

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
    public User getByName(String name) {
        try (Session session = sessionFactory.openSession()) {
//            Query<User> query = session.createQuery("from User where first_name = :name", User.class);
//            query.setParameter("first_name", name);
//            List<User> result = query.list();
//            if (result.isEmpty()) {
//                throw new EntityNotFoundException("User", "first name", name);
//            }
//            return result.get(0);
        }
        return null;
    }

    @Override
    public User getById(int id) {
        try (Session session = sessionFactory.openSession()) {
//            Query<User> query = session.createQuery("from User where user_id = :id", User.class);
//            query.setParameter("user_id", id);
//            List<User> result = query.list();
//            if (result.isEmpty()) {
//                throw new EntityNotFoundException("User", "id", id);
//            }
//            return result.get(0);
        }
        return null;
    }

    @Override
    public List<User> getAll() {
//        try (Session session = sessionFactory.openSession()) {
//            Query<User> query = session.createQuery("from User", User.class);
//            return query.list();
//        }
        return null;
    }

    @Override
    public User getByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
//            Query<User> query = session.createQuery("from User where username = :username", User.class);
//            query.setParameter("username", username);
//            List<User> result = query.list();
//            if (result.isEmpty()) {
//                throw new EntityNotFoundException("User", "username", username);
//            }
//            return result.get(0);
        }
        return null;
    }

    @Override
    public User getByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
//            Query<User> query = session.createQuery("from User where email = :email", User.class);
//            query.setParameter("email", email);
//            List<User> result = query.list();
//            if (result.isEmpty()) {
//                throw new EntityNotFoundException("Email", email);
//            }
//            return result.get(0);
        }
        return null;
    }
}
