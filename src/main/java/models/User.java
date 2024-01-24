package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int id;

    @Column(name = "is_admin")
    private boolean isAdmin;

    @Column(name = "username")
    @NotNull(message = "Username can't be empty!")
    @Size(min = 4, max = 50, message = "Username should be between 6 and 50 symbols!")
    private String username;

    @JsonIgnore
    @Column(name = "password")
    @NotNull(message = "Password can't be empty!")
    @Size(min = 8, max = 50, message = "Password should be between 8 and 50 symbols!")
    private String password;

    @Column(name = "first_name")
    @NotNull(message = "First name can't be empty!")
    @Size(min = 2, max = 32, message = "First name should be between 4 and 32 symbols!")
    private String firstName;

    @Column(name = "last_name")
    @NotNull(message = "Last name can't be empty!")
    @Size(min = 2, max = 32, message = "Last name should be between 2 and 32 symbols!")
    private String lastName;

    @Column(name = "email")
    @NotNull(message = "Email can't be empty!")
    @Size(min = 10, max = 100, message = "Email should be between 10 and 100 symbols!")
    private String email;
    @Column(name = "is_blocked")
    private String isBlocked;

    public User(boolean isAdmin, int id, String username, String password, String firstName, String lastName, String email, String isBlocked) {
        this.isAdmin = isAdmin;
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.isBlocked = isBlocked;
    }

    public User() {
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(String isBlocked) {
        this.isBlocked = isBlocked;
    }
}
