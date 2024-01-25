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
    public void create(User user) {
        boolean usernameExists = true;
        try {
            userRepository.getByUsername(user.getUsername());
        }catch (EntityNotFoundException e) {
            usernameExists = false;
        }
        if (usernameExists) {
            throw new DuplicateExistsException("User", "username", user.getUsername());
        }

        emailValidator(user.getEmail());
        boolean emailExists = true;
        try {
            userRepository.getByEmail(user.getEmail());
        }catch (EntityNotFoundException e){
            emailExists = false;
        }
        if (emailExists) {
            throw new DuplicateExistsException("User", "email", user.getEmail());
        }

        userRepository.create(user);
    }

    @Override
    public void delete(User user, User deletedBy) {
        if (!deletedBy.isAdmin()) {
            throw new UnauthorizedOperationException("Only admins can delete users!");
        }
        userRepository.delete(user);
    }

    @Override
    public void update(User user, User updatedBy) {
        if (!user.equals(updatedBy)) {
            throw new UnauthorizedOperationException("Only the user can modify it's data!");
        }
        if (!user.getUsername().equals(updatedBy.getUsername())) {
            throw new UnauthorizedOperationException("Username cannot be changed");
        }
        boolean emailExists = true;
        try {
            userRepository.getByEmail(user.getEmail());
        }catch (EntityNotFoundException e){
            emailExists = false;
        }
        if (emailExists) {
            throw new DuplicateExistsException("User", "email", user.getEmail());
        }

        userRepository.update(user);
    }

    @Override
    public User getByName(String name, User user) {
        if (!user.isAdmin()) {
            throw new UnauthorizedOperationException("Only admins have access to the requested functionality!");
        }
        return userRepository.getByName(name);
    }

    @Override
    public User getByUsername(String username, User user) {
        if (!user.isAdmin()) {
            throw new UnauthorizedOperationException("Only admins have access to the requested functionality!");
        }
        return userRepository.getByUsername(username);
    }

    @Override
    public User getById(int id, User user) {
        if (!user.isAdmin()) {
            throw new UnauthorizedOperationException("Only admins have access to the requested functionality!");
        }
        return userRepository.getById(id);
    }

    @Override
    public User getByEmail(String email, User user) {
        if (!user.isAdmin()) {
            throw new UnauthorizedOperationException("Only admins have access to the requested functionality!");
        }
        return userRepository.getByEmail(email);
    }

    @Override
    public void blockUser(String username, User admin) {
        if (!admin.isAdmin()) {
            throw new UnauthorizedOperationException("Only admins have access to the requested functionality!");
        }
        User userToBlock = userRepository.getByUsername(username);
        userToBlock.setIsBlocked(true);
        userRepository.update(userToBlock);
    }

    @Override
    public void unblockUser(String username, User admin) {
        if (!admin.isAdmin()) {
            throw new UnauthorizedOperationException("Only admins have access to the requested functionality!");
        }
        User userToUnblock = userRepository.getByUsername(username);
        userToUnblock.setIsBlocked(false);
        userRepository.update(userToUnblock);
    }

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    private String emailValidator(String email) {
        String MAIL_REGEX = "^[a-zA-Z]+@[a-zA-Z]+\\.[a-zA-Z]+$";
        Pattern MAIL_PATTERN = Pattern.compile(MAIL_REGEX);

        Matcher matcher = MAIL_PATTERN.matcher(email);
        if (matcher.matches()) {
            return email;
        }
        else {
            throw new InvalidEmailException(email);
        }
    }
}
