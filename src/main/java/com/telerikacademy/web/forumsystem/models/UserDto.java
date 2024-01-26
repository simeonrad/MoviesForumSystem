package com.telerikacademy.web.forumsystem.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserDto {

    private int id;

    @NotNull(message = "Username can't be empty!")
    @Size(min = 4, max = 50, message = "Username should be between 4 and 50 symbols!")
    private String username;
    @NotNull(message = "Password can't be empty!")
    @Size(min = 8, max = 50, message = "Password should be between 8 and 50 symbols!")
    private String password;

    @NotNull(message = "First name can't be empty!")
    @Size(min = 4, max = 32, message = "First name should be between 4 and 32 symbols!")
    private String firstName;

    @NotNull(message = "Last name can't be empty!")
    @Size(min = 2, max = 32, message = "Last name should be between 2 and 32 symbols!")
    private String lastName;

    @NotNull(message = "Email can't be empty!")
    @Size(min = 10, max = 100, message = "Email should be between 10 and 100 symbols!")
    private String email;

    public UserDto() {
    }

    public UserDto(String username, String firstName, String lastName, String email) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
