package repositories;

import exceptions.EntityNotFoundException;
import models.Tag;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TagRepositoryImpl implements TagRepository {

    private final SessionFactory sessionFactory;

    public TagRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(Tag tag) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(tag);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Tag tag) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(tag);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Tag tag) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(tag);
            session.getTransaction().commit();
        }
    }

    @Override
    public Tag getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Tag tag = session.get(Tag.class, id);
            if (tag == null) {
                throw new EntityNotFoundException("Tag", id);
            }
            return tag;
        }
    }

    @Override
    public Tag getByName(String name) {
        try (Session session = sessionFactory.openSession()) {
//            Query<Tag> query = session.createQuery("from Tag where name = :tag_name", Tag.class);
//            query.setParameter("tag_name", name);
//            List<Tag> result = query.list();
//            if (result.isEmpty()) {
//                throw new EntityNotFoundException("Tag", "name", name);
//            }
//            return result.get(0);
        }
        return null;
    }
}