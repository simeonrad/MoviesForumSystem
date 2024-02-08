package com.telerikacademy.web.forumsystem.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @Column(name = "content")
    private String content;

    @Column(name = "date_created")
    private LocalDateTime timeStamp;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "repliedTo_id")
    private Comment repliedTo;

    public Comment() {
        this.timeStamp = LocalDateTime.now();
    }

    public Comment(String content) {
        this.content = content;
        this.timeStamp = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Comment getRepliedTo() {
        return repliedTo;
    }

    public void setRepliedTo(Comment repliedTo) {
        this.repliedTo = repliedTo;
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

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
