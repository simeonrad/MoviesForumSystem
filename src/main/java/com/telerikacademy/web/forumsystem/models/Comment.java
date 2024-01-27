package com.telerikacademy.web.forumsystem.models;

import jakarta.persistence.*;
import com.telerikacademy.web.forumsystem.models.User;

import java.time.LocalDate;

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
    private LocalDate timeStamp;

    @Column(name = "post_id")
    private int post;
    @ManyToOne
    @JoinColumn(name = "repliedTo_id")
    private Comment repliedTo;

    public Comment() {
        this.timeStamp = LocalDate.now();
    }

    public Comment(String content) {
        this.content = content;
        this.timeStamp = LocalDate.now();
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

    public LocalDate getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDate timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getPost() {
        return post;
    }

    public void setPost(int post) {
        this.post = post;
    }
}
