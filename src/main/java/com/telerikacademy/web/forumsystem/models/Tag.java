package com.telerikacademy.web.forumsystem.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private int id;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "posts_tags",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private Set<Tag> tagSet;

    @ManyToMany(mappedBy = "tags")
    private Set<Post> posts = new HashSet<>();
    // getters and setters

    @Column(name = "tag_name")
    private String name;

    public Tag(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public Tag() {

    }

    public String getName() {
        return name;
    }

    public Set<Tag> getTagSet() {
        return tagSet;
    }

    public void setTagSet(Set<Tag> tagSet) {
        this.tagSet = tagSet;
    }

    public void addToTagList(Tag tag) {
        tagSet.add(tag);
    }

    public void removeFromTagList(Tag tag) {
        tagSet.remove(tag);
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
