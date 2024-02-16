package com.telerikacademy.web.forumsystem.services;

import com.telerikacademy.web.forumsystem.exceptions.EntityNotFoundException;
import com.telerikacademy.web.forumsystem.models.Post;
import com.telerikacademy.web.forumsystem.models.Tag;
import com.telerikacademy.web.forumsystem.repositories.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TagServiceTests {
    @Mock
    private TagRepository tagRepository;
    @InjectMocks
    private TagServiceImpl tagService;
    private ArgumentCaptor<Tag> tagCaptor = ArgumentCaptor.forClass(Tag.class);
    private ArgumentCaptor<Set<Tag>> tagsCaptor = ArgumentCaptor.forClass(Set.class);

    @Test
    public void createTag_Success() {
        String tagName = "Technology";
        Tag tag = new Tag();
        tag.setName(tagName);
        doNothing().when(tagRepository).create(any(Tag.class));
        tagService.create(tagName);
        verify(tagRepository).create(tagCaptor.capture());
        assertEquals(tagName, tagCaptor.getValue().getName());
    }

    @Test
    public void updateTag_Success() {
        Tag existingTag = new Tag("Technology", 1);
        when(tagRepository.getById(existingTag.getId())).thenReturn(existingTag);
        Tag updatedTag = new Tag("Tech", 1);
        tagService.update(updatedTag);
        verify(tagRepository).update(tagCaptor.capture());
        assertEquals("tech", tagCaptor.getValue().getName());
    }

    @Test
    public void updateTag_TagNotFound_ThrowsEntityNotFoundException() {
        int tagId = 1;
        when(tagRepository.getById(tagId)).thenReturn(null);
        Tag tagToUpdate = new Tag("Nonexistent", tagId);
        assertThrows(EntityNotFoundException.class, () -> tagService.update(tagToUpdate));
    }

    @Test
    public void addTagsToPost_Success() {
        Post post = new Post();
        Set<Tag> tags = new HashSet<>(Arrays.asList(new Tag("Tech", 1), new Tag("Science", 2)));
        doNothing().when(tagRepository).addTagsToPost(anySet(), any(Post.class));
        tagService.addTagsToPost(tags, post);
        verify(tagRepository).addTagsToPost(tagsCaptor.capture(), eq(post));
        assertEquals(2, tagsCaptor.getValue().size());
    }

    @Test
    public void getTagByName_Success() {
        String tagName = "Technology";
        Tag expectedTag = new Tag(tagName, 1);
        when(tagRepository.getByName(tagName)).thenReturn(expectedTag);
        Tag resultTag = tagService.getByName(tagName);
        assertEquals(expectedTag, resultTag);
    }

    @Test
    public void getById_TagExists_ReturnsTag() {
        Tag expectedTag = new Tag("Tech", 1);
        when(tagRepository.getById(1)).thenReturn(expectedTag);
        Tag actualTag = tagService.getById(1);
        assertEquals(expectedTag, actualTag);
    }

    @Test
    public void getByName_TagExists_ReturnsTag() {
        Tag expectedTag = new Tag("Tech", 1);
        when(tagRepository.getByName("Tech")).thenReturn(expectedTag);
        Tag actualTag = tagService.getByName("Tech");
        assertEquals(expectedTag, actualTag);
    }

    @Test
    public void getByName_TagDoesNotExist_ReturnsNull() {
        when(tagRepository.getByName("Nonexistent")).thenReturn(null);
        Tag result = tagService.getByName("Nonexistent");
        assertNull(result);
    }

    @Test
    public void deleteTag_TagExists_TagIsDeleted() {
        int tagId = 1;
        Tag existingTag = new Tag("Tech", tagId);
        when(tagRepository.getById(tagId)).thenReturn(existingTag);
        tagService.delete(existingTag);
        verify(tagRepository).delete(existingTag);
    }

    @Test
    public void deleteTag_TagDoesNotExist_ThrowsEntityNotFoundException() {
        int tagId = 1;
        Tag nonExistentTag = new Tag("Tech", tagId);
        when(tagRepository.getById(tagId)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> tagService.delete(nonExistentTag));
    }



}
