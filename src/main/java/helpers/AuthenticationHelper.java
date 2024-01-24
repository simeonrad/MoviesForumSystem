package helpers;

import exceptions.EntityNotFoundException;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import services.UserService;

@Component
public class AuthenticationHelper {
    public static final String USERNAME_HEADER_NAME = "username";
    public static final String PASSWORD_HEADER_NAME = "password";
    private final UserService service;

    @Autowired
    public AuthenticationHelper(UserService service) {
        this.service = service;
    }

    public User tryGetUser(HttpHeaders headers) {
        if (!headers.containsKey(USERNAME_HEADER_NAME) || !headers.containsKey(PASSWORD_HEADER_NAME)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "You need to log in in order to retrieve the required resource!");
        }

        try {
            String username = headers.getFirst(USERNAME_HEADER_NAME);
            String password = headers.getFirst(PASSWORD_HEADER_NAME);
            User user = service.getByUsername(username);
            if (!user.getPassword().equals(password)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Invalid authentication! Password is incorrect!");
            }
            return user;
        }
        catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Invalid username!");
        }
    }
}
