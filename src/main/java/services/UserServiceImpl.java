package services;

import exceptions.DuplicateExistsException;
import exceptions.EntityNotFoundException;
import exceptions.InvalidEmailException;
import exceptions.UnauthorizedOperationException;
import models.User;
import org.springframework.stereotype.Service;
import repositories.UserRepository;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void create(User user, User createdBy) {
//        boolean usernameExists = true;
//        try {
//            userRepository.getByUsername(user.getUsername());
//        }catch (EntityNotFoundException e) {
//            usernameExists = false;
//        }
//        if (usernameExists) {
//            throw new DuplicateExistsException("User", "username", user.getUsername());
//        }
//
//        emailValidator(user.getEmail());
//        boolean emailExists = true;
//        try {
//            userRepository.getByEmail(user.getEmail());
//        }catch (EntityNotFoundException e){
//            emailExists = false;
//        }
//        if (emailExists) {
//            throw new DuplicateExistsException("User", "email", user.getEmail());
//        }

        userRepository.create(user);
    }

    @Override
    public void delete(User user, User deletedBy) {
//        if (!deletedBy.isAdmin()) {
//            throw new UnauthorizedOperationException("Only admins can delete users!");
//        }
        userRepository.delete(user);
    }

    @Override
    public void update(User user, User updatedBy) {
//        if (!user.equals(updatedBy)) {
//            throw new UnauthorizedOperationException("Only the user can modify it's data!");
//        }
//        boolean duplicateExists = true;
//
//        try {
//            if (user.getEmail()) {
//                duplicateExists = false;
//            }
//        } catch (EntityNotFoundException e) {
//            duplicateExists = false;
//        }
//        if (duplicateExists) {
//            throw new DuplicateExistsException("User", "name", user.getName());
//        }
//        userRepository.update(user);
    }

    @Override
    public User getByName(String name) {
        return userRepository.getByName(name);
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.getByUsername(username);
    }

    @Override
    public User getById(int id) {
        return userRepository.getById(id);
    }

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    private String emailValidator(String mail) {
        String MAIL_REGEX = "^[a-zA-Z]+@[a-zA-Z]+\\.[a-zA-Z]+$";
        Pattern MAIL_PATTERN = Pattern.compile(MAIL_REGEX);

        Matcher matcher = MAIL_PATTERN.matcher(mail);
        if (matcher.matches()) {
            return mail;
        }
        else {
            throw new InvalidEmailException(mail);
        }
    }
}
