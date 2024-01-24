package models;

import jakarta.persistence.*;

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

    public Comment() {

    }

    public void create(Comment comment) {

    }

    public Comment(String content) {
        this.content = content;
        this.timeStamp = LocalDate.now();
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
}
