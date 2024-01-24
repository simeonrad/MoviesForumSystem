package models;

import java.time.LocalDate;

public class Comment {
    private User author;
    private String content;
    private int likes;
    private LocalDate timeStamp;
    public void create(Comment comment) {

    }

    public Comment(String content) {
        this.content = content;
        this.timeStamp =LocalDate.now();
        this.likes = 0;
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
