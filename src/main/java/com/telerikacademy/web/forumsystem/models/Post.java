package com.telerikacademy.web.forumsystem.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;


import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "posts")

public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private int id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;
    @Column(name = "content")
    private String content;
    @Column(name = "likes")
    private int likes;
    @Column(name = "date_created")
    private LocalDate timeStamp;
    @Column(name = "title")
    private String title;
    @OneToMany
    @JoinColumn(name = "post_id")
    private Set<Comment> comments;

    @ManyToMany
    @JoinTable(
            name = "posts_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_likes",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likedByUsers;

    public Post(String content, String title) {
        this.title = title;
        this.content = content;
        this.timeStamp = LocalDate.now();
        this.likes = 0;
    }

    public Post() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public LocalDate getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDate timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void addLike(User user) {
        this.likedByUsers.add(user);
    }

    public Set<User> getLikedByUsers() {
        return likedByUsers;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}
