package com.telerikacademy.web.forumsystem.controllers;

import com.telerikacademy.web.forumsystem.exceptions.*;
import com.telerikacademy.web.forumsystem.helpers.UserShow;
import com.telerikacademy.web.forumsystem.helpers.UserShowAdmin;
import com.telerikacademy.web.forumsystem.models.FilterOptions;
import com.telerikacademy.web.forumsystem.models.User;
import com.telerikacademy.web.forumsystem.models.UserDto;
import com.telerikacademy.web.forumsystem.repositories.UserRepository;
import com.telerikacademy.web.forumsystem.services.UserService;
import com.telerikacademy.web.forumsystem.helpers.AuthenticationHelper;
import com.telerikacademy.web.forumsystem.helpers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationHelper authenticationHelper;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserRepository userRepository, AuthenticationHelper authenticationHelper, UserMapper userMapper) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.authenticationHelper = authenticationHelper;
        this.userMapper = userMapper;
    }

    @PostMapping()
    public UserShow createUser(@RequestBody UserDto userDto) {
        try {
            User user = userMapper.fromDto(userDto);
            userService.create(user);
            return userMapper.toDto(user);
        } catch (DuplicateExistsException de) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, de.getMessage());
        } catch (InvalidEmailException ie) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ie.getMessage());
        }
    }

    @PutMapping()
    public UserShow updateUser(@RequestHeader HttpHeaders headers, @RequestBody User user) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            User updatedUser = userMapper.fromDtoUpdate(user);
            userService.update(updatedUser, currentUser);
            return userMapper.toDto(updatedUser);
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateExistsException de) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, de.getMessage());
        }
    }

    @DeleteMapping()
    public UserShow deleteUser(@RequestHeader HttpHeaders headers, @RequestBody UserDto userDto) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            User user = userMapper.fromDto(userDto);
            userService.delete(user, currentUser);
            user = userRepository.getByUsername(user.getUsername());
            return userMapper.toDto(user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        }
    }

    @PutMapping("/block")
    public UserShowAdmin blockUser(@RequestBody String username, @RequestHeader HttpHeaders headers) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            userService.blockUser(username, currentUser);
            User blockedUser = userRepository.getByUsername(username);
            return userMapper.toDtoAdmin(blockedUser);
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/unblock")
    public UserShowAdmin unblockUser(@RequestBody String username, @RequestHeader HttpHeaders headers) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            userService.unblockUser(username, currentUser);
            User unblockedUser = userRepository.getByUsername(username);
            return userMapper.toDtoAdmin(unblockedUser);
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/makeAdmin")
    public UserShowAdmin makeUserAdmin(@RequestBody String username, @RequestHeader HttpHeaders headers) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            userService.makeAdmin(username, currentUser);
            User admin = userRepository.getByUsername(username);
            return userMapper.toDtoAdmin(admin);
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/unmakeAdmin")
    public UserShowAdmin unmakeUserAdmin(@RequestBody String username, @RequestHeader HttpHeaders headers) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            userService.unmakeAdmin(username, currentUser);
            User admin = userRepository.getByUsername(username);
            return userMapper.toDtoAdmin(admin);
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping
    public List<User> get(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestHeader HttpHeaders headers) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            FilterOptions filterOptions = new FilterOptions(email, username, firstName, sortBy, sortOrder);
            return userService.get(filterOptions, currentUser);
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (InvalidParameterException ip) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ip.getMessage());
        }
    }
}