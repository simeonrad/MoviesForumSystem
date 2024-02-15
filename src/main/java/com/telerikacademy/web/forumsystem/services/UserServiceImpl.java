package com.telerikacademy.web.forumsystem.services;


import com.telerikacademy.web.forumsystem.exceptions.DuplicateExistsException;
import com.telerikacademy.web.forumsystem.exceptions.EntityNotFoundException;
import com.telerikacademy.web.forumsystem.exceptions.InvalidEmailException;
import com.telerikacademy.web.forumsystem.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.forumsystem.helpers.UserMapper;
import com.telerikacademy.web.forumsystem.models.FilterOptions;
import com.telerikacademy.web.forumsystem.models.PhoneNumber;
import com.telerikacademy.web.forumsystem.models.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.telerikacademy.web.forumsystem.repositories.UserRepository;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public void create(@Valid User user) {
        boolean usernameExists = true;
        try {
            User userCreated = userRepository.getByUsername(user.getUsername());
            if (userCreated.isDeleted()) {
                user = userMapper.fromDtoUpdate(user);
                user.setDeleted(false);
                emailValidator(user.getEmail());
                boolean emailExists = true;
                try {
                    user = userRepository.getByEmail(user.getEmail());
                } catch (EntityNotFoundException e) {
                    emailExists = false;
                }
                if (emailExists) {
                    throw new DuplicateExistsException("User", "email", user.getEmail());
                }
                userRepository.update(user);
                return;
            }
        } catch (EntityNotFoundException e) {
            usernameExists = false;
        }
        if (usernameExists && !user.isDeleted()) {
            throw new DuplicateExistsException("User", "username", user.getUsername());
        }

        emailValidator(user.getEmail());
        boolean emailExists = true;
        try {
            user = userRepository.getByEmail(user.getEmail());
        } catch (EntityNotFoundException e) {
            emailExists = false;
        }
        if (emailExists) {
            throw new DuplicateExistsException("User", "email", user.getEmail());
        }
        userRepository.create(user);
    }

    @Override
    public void update(User user, User updatedBy) {
        if (!user.equals(updatedBy)) {
            throw new UnauthorizedOperationException("Only the user can modify it's data!");
        }
        if (!user.getUsername().equals(updatedBy.getUsername())) {
            throw new UnauthorizedOperationException("Username cannot be changed");
        }
        boolean emailExists = userRepository.updateEmail(user.getEmail());
        if (emailExists) {
            throw new DuplicateExistsException("User", "email", user.getEmail());
        }
        userRepository.update(user);

    }

    @Override
    public void update(User user) {
        boolean emailExists = userRepository.updateEmail(user.getEmail());
        emailValidator(user.getEmail());

        if (emailExists) {
            throw new DuplicateExistsException("User", "email", user.getEmail());
        }
        userRepository.update(user);
    }

    @Override
    public void delete(User user, User deletedBy) {
        user = userRepository.getByUsername(user.getUsername());
        if (user.isDeleted()) {
            throw new EntityNotFoundException("User", "username", user.getUsername());
        }
        if (!deletedBy.isAdmin() && !user.getUsername().equals(deletedBy.getUsername())) {
            throw new UnauthorizedOperationException("Only admins or the same user can delete user profiles!");
        }
        user.setDeleted(true);
        userRepository.delete(user);
    }

    @Override
    public void delete(User user) {
        if (user.isDeleted()) {
            throw new EntityNotFoundException("User", "username", user.getUsername());
        }
        user.setDeleted(true);
        userRepository.delete(user);
    }

    @Override
    public List<User> get(FilterOptions filterOptions, User user) {
        if (!user.isAdmin()) {
            throw new UnauthorizedOperationException("Only admins have access to the requested functionality!");
        }
        return userRepository.get(filterOptions);
    }

    public User get(String email){
       return userRepository.getByEmail(email);
    }

    @Override
    public Page<User> get(FilterOptions filterOptions, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.get(filterOptions, pageable);
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
    public void makeAdmin(String username, String phoneNumber, User admin) {
        User user = userRepository.getByUsername(username);
        if (user.isAdmin()) {
            throw new UnauthorizedOperationException(String.format("User with username %s is already an admin", username));
        }
        user.setAdmin(true);
        if (phoneNumber != null) {
            PhoneNumber number = new PhoneNumber();
            number.setPhoneNumber(phoneNumber);
            number.setUser(user);
            userRepository.addPhone(number);
        }
        userRepository.update(user);
    }

    @Override
    public void addPhoneNumber(String phoneNumber, User user) {
        PhoneNumber number = new PhoneNumber();
        number.setPhoneNumber(phoneNumber);
        number.setUser(user);
        userRepository.addPhone(number);
    }

    @Override
    public void addProfilePhoto(String photoUrl, User user) {
        user.setProfilePhotoUrl(photoUrl);
        userRepository.update(user);
    }

    @Override
    public void makeAdmin(String username) {
        User user = userRepository.getByUsername(username);
        user.setAdmin(true);
        userRepository.update(user);
    }

    @Override
    public void unmakeAdmin(String username, User admin) {
        User user = userRepository.getByUsername(username);
        if (!user.isAdmin()) {
            throw new UnauthorizedOperationException(String.format("User with username %s is not an admin", username));
        }
        user.setAdmin(false);
        userRepository.update(user);
    }

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }


    String emailValidator(String email) {
        String MAIL_REGEX = "^[a-zA-Z]+@[a-zA-Z]+\\.[a-z]+$";
        Pattern MAIL_PATTERN = Pattern.compile(MAIL_REGEX);

        Matcher matcher = MAIL_PATTERN.matcher(email);
        if (matcher.matches()) {
            return email;
        } else {
            throw new InvalidEmailException(email);
        }
    }
}