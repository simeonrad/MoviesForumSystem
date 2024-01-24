package models;

import jakarta.persistence.*;

import java.time.LocalDate;

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

    public Post(String content, String title) {
        this.title = title;
        this.content = content;
        this.timeStamp = LocalDate.now();
        this.likes = 0;
    }

    public Post() {

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
}
