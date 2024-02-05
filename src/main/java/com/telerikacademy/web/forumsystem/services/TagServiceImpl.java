package com.telerikacademy.web.forumsystem.services;

import com.telerikacademy.web.forumsystem.exceptions.EntityNotFoundException;
import com.telerikacademy.web.forumsystem.models.Post;
import com.telerikacademy.web.forumsystem.models.Tag;

import org.springframework.stereotype.Service;
import com.telerikacademy.web.forumsystem.repositories.TagRepository;

import java.util.Set;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public void create(String tag) {
        Tag tagCreated = new Tag();
        tagCreated.setName(tag);
        tagRepository.create(tagCreated);
    }

    @Override
    public void update(Tag tag) {
        Tag existingTag = tagRepository.getById(tag.getId());
        if (existingTag == null) {
            throw new EntityNotFoundException("Tag", "id", Integer.toString(tag.getId()));
        }
        tag.setName(tag.getName().toLowerCase());
        tagRepository.update(tag);
    }

    @Override
    public void delete(Tag tag) {
        Tag existingTag = tagRepository.getById(tag.getId());
        if (existingTag == null) {
            throw new EntityNotFoundException("Tag", "id", Integer.toString(tag.getId()));
        }
        tagRepository.delete(tag);
    }

    @Override
    public void addTagsToPost(Set<Tag> tags, Post post) {
        tagRepository.addTagsToPost(tags, post);
    }

    @Override
    public Tag getById(int id) {
        return tagRepository.getById(id);
    }

    @Override
    public Tag getByName(String name) {
        return tagRepository.getByName(name);
    }
}
