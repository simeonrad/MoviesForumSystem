package com.telerikacademy.web.forumsystem.repositories;

import com.telerikacademy.web.forumsystem.exceptions.EntityNotFoundException;
import com.telerikacademy.web.forumsystem.models.Post;
import com.telerikacademy.web.forumsystem.models.Tag;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class TagRepositoryImpl implements TagRepository {

    private final SessionFactory sessionFactory;

    private final PostRepository postRepository;

    public TagRepositoryImpl(SessionFactory sessionFactory, PostRepository postRepository) {
        this.sessionFactory = sessionFactory;
        this.postRepository = postRepository;
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
    public void addTagsToPost(Set<Tag> tags, Post post) {
        Tag[] tagsArray = tags.toArray(new Tag[tags.size()]);
        for (Tag tag : tagsArray) {
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();
                Tag existingTag = session.get(Tag.class, tag.getId());
                if (existingTag == null) {
                    session.persist(tag);
                } else {
                    session.merge(tag);
                }
                session.getTransaction().commit();
            }
        }
        post.setTags(tags);
        postRepository.update(post);
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
            Query<Tag> query = session.createQuery("from Tag where name = :tag_name", Tag.class);
            query.setParameter("tag_name", name);
            List<Tag> result = query.list();
            if (result.isEmpty()) {
                throw new EntityNotFoundException("Tag", "name", name);
            }
            return result.get(0);
        }
    }


}