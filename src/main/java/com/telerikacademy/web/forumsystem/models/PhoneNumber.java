package com.telerikacademy.web.forumsystem.models;

import jakarta.persistence.*;

@Entity
@Table(name = "phone_numbers")
public class PhoneNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "phone_number_id")
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Column(name = "phone_number")
    private String phoneNumber;

    public PhoneNumber() {
    }

    public PhoneNumber(int id, User user, String phoneNumber) {
        this.id = id;
        this.user = user;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
