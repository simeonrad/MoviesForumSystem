package com.telerikacademy.web.forumsystem.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class RegisterDto extends LoginDto {

    @NotEmpty
    private String passwordConfirm;
    @NotEmpty
    @Size(min = 4, max = 32, message = "First name should be between 4 and 32 symbols!")
    private String firstName;
    @NotEmpty
    @Size(min = 2, max = 32, message = "Last name should be between 2 and 32 symbols!")
    private String lastName;
    @NotEmpty
    @Email
    private String email;

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
