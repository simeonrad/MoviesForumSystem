package services;

import exceptions.DuplicateExistsException;
import exceptions.EntityNotFoundException;
import models.Tag;
import models.User;
import org.springframework.stereotype.Service;
import repositories.TagRepository;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public void create(Tag tag, User user) {
        Tag existingTag = tagRepository.getByName(tag.getName());
        if (existingTag != null) {
            throw new DuplicateExistsException("Tag", "name", tag.getName());
        }
        tag.setName(tag.getName().toLowerCase());
        tagRepository.create(tag);
    }

    @Override
    public void update(Tag tag, User user) {
        Tag existingTag = tagRepository.getById(tag.getId());
        if (existingTag == null) {
            throw new EntityNotFoundException("Tag", "id", Integer.toString(tag.getId()));
        }
        tag.setName(tag.getName().toLowerCase());
        tagRepository.update(tag);
    }

    @Override
    public void delete(Tag tag, User user) {
        Tag existingTag = tagRepository.getById(tag.getId());
        if (existingTag == null) {
            throw new EntityNotFoundException("Tag", "id", Integer.toString(tag.getId()));
        }
        tagRepository.delete(tag);
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
