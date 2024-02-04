package com.telerikacademy.web.forumsystem.helpers;

import com.telerikacademy.web.forumsystem.models.Post;
import com.telerikacademy.web.forumsystem.models.PostDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostMapper {
    public PostDto toDto(Post post) {
        if (post == null) {
            return null;
        }

        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setAuthorId(post.getAuthor().getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setLikes(post.getLikes());
        dto.setTimeStamp(post.getTimeStamp());
        dto.setTag(post.getTags());
        return dto;
    }

    public Post fromDto(PostDto dto) {
        if (dto == null) {
            return null;
        }
        Post post = new Post();
        post.setId(dto.getId());
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setLikes(dto.getLikes());
        post.setTimeStamp(dto.getTimeStamp());
        //post.setTags(dto.getTag());
        return post;
    }

    public List<PostDto> toDtoList(List<Post> posts) {
        if (posts == null) {
            return null;
        }
        return posts.stream().map(this::toDto).collect(Collectors.toList());
    }

    public void updateFromDto(PostDto dto, Post post) {
        if (dto.getTitle() != null) {
            post.setTitle(dto.getTitle());
        }
        if (dto.getContent() != null) {
            post.setContent(dto.getContent());
        }
    }
}