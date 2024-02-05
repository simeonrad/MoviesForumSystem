package com.telerikacademy.web.forumsystem.helpers;

import com.telerikacademy.web.forumsystem.exceptions.EntityNotFoundException;
import com.telerikacademy.web.forumsystem.models.Tag;
import com.telerikacademy.web.forumsystem.repositories.TagRepository;
import com.telerikacademy.web.forumsystem.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class TagHelper {
    public static final String TAGS_HEADER_NAME = "tags";
    private final TagService service;
    private final TagRepository repository;

    @Autowired
    public TagHelper(TagService service, TagRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    public Set<Tag> tryGetTags(HttpHeaders headers) {
        if (!headers.containsKey(TAGS_HEADER_NAME)) {
            return null;
        }
        Set<Tag> tagSet = new HashSet<>();
        String tags = headers.getFirst(TAGS_HEADER_NAME);
        //return new HashSet<>(Arrays.asList(tags.split(",")));     }     return new HashSet<>();
        if (tags != null) {
            tags = tags.toLowerCase();
            String[] tagsSet = tags.split(", ");
            for (int i = 0; i < tagsSet.length; i++) {
                try {
                    Tag tag = repository.getByName(tagsSet[i]);
                    tagSet.add(tag);
                } catch (EntityNotFoundException e) {
                    service.create(tagsSet[i]);
                    Tag tag = repository.getByName(tagsSet[i]);
                    tagSet.add(tag);
                }
            }
        }
        return tagSet;
    }
}
