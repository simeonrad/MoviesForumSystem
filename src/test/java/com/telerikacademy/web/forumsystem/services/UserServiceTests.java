package com.telerikacademy.web.forumsystem.services;

import com.telerikacademy.web.forumsystem.exceptions.DuplicateExistsException;
import com.telerikacademy.web.forumsystem.exceptions.EntityNotFoundException;
import com.telerikacademy.web.forumsystem.exceptions.InvalidEmailException;
import com.telerikacademy.web.forumsystem.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.forumsystem.helpers.UserMapper;
import com.telerikacademy.web.forumsystem.models.FilterOptions;
import com.telerikacademy.web.forumsystem.models.PhoneNumber;
import com.telerikacademy.web.forumsystem.models.User;
import com.telerikacademy.web.forumsystem.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserMapper userMapper;


    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        UserMapper userMapper = mock(UserMapper.class);
        userService = new UserServiceImpl(userRepository, userMapper);
    }


    @Test
    public void createUser_WhenUserDoesNotExist_UserIsCreated() {
        User mockUser = new User();
        mockUser.setUsername("testUsername");
        mockUser.setEmail("testEmail@email.com");
        when(userRepository.getByUsername(mockUser.getUsername())).thenThrow(new EntityNotFoundException("User", "username", mockUser.getUsername()));
        when(userRepository.getByEmail(mockUser.getEmail())).thenThrow(new EntityNotFoundException("User", "email", mockUser.getEmail()));

        userService.create(mockUser);

        verify(userRepository).create(mockUser);
    }

//    @Test
//    public void createUser_WhenUserExistsAndIsDeleted_UpdatesUser() {
//        User existingUser = new User();
//        existingUser.setDeleted(true);
//        User newUser = new User();
//        newUser.setUsername("username");
//
//        when(userRepository.getByUsername(newUser.getUsername())).thenReturn(existingUser);
//
//        when(userMapper.fromDtoUpdate(newUser)).thenReturn(existingUser);
//
//        userService.create(newUser);
//
//        verify(userMapper).fromDtoUpdate(newUser);
//        verify(userRepository).update(existingUser);
//    }

    @Test
    public void createUser_WhenUsernameExistsAndNotDeleted_ThrowsException() {
        User existingUser = new User();
        existingUser.setDeleted(false);
        User newUser = new User();
        newUser.setUsername("username");

        when(userRepository.getByUsername(newUser.getUsername())).thenReturn(existingUser);

        assertThrows(DuplicateExistsException.class, () -> userService.create(newUser));
    }

//    @Test
//    public void createUser_WhenEmailExists_ThrowsException() {
//        User newUser = new User();
//        newUser.setEmail("email@example.com");
//        newUser.setUsername("username");
//
//        lenient().when(userRepository.getByUsername(newUser.getUsername())).thenReturn(newUser);
//
//        lenient().when(userRepository.getByEmail(newUser.getEmail())).thenReturn(new User());
//
//        assertThrows(DuplicateExistsException.class, () -> userService.create(newUser));
//    }


    @Test
    public void updateUser_WhenAuthorized_UserIsUpdated() {
        User user = new User();
        user.setUsername("testUsername");
        user.setEmail("testEmail@email.com");
        User updatedBy = user;

        userService.update(user, updatedBy);

        verify(userRepository).update(user);
    }

    @Test
    public void updateUser_WhenUnauthorized_ThrowsException() {
        User user = new User();
        user.setUsername("testUsername");
        user.setEmail("testEmail@email.com");
        User updatedBy = new User();
        updatedBy.setUsername("testWrongUsername");
        updatedBy.setEmail("testWrongEmail@email.com");

        assertThrows(UnauthorizedOperationException.class, () -> userService.update(user, updatedBy));
    }

    @Test
    public void update_WhenUsernameChanged_ThrowsUnauthorizedOperationException() {
        User user = new User();
        user.setId(1);
        user.setUsername("originalUsername");

        User updatedBy = new User();
        updatedBy.setId(1);
        updatedBy.setUsername("originalUsername");

        updatedBy.setUsername("newUsername");

        assertThrows(UnauthorizedOperationException.class, () -> userService.update(user, updatedBy));
    }

    @Test
    public void update_WhenEmailAlreadyExists_ThrowsDuplicateExistsException() {
        User user = new User();
        user.setEmail("email@example.com");
        user.setUsername("originalUsername");

        when(userRepository.updateEmail(user.getEmail())).thenReturn(true);

        assertThrows(DuplicateExistsException.class, () -> userService.update(user, user));
    }

    @Test
    public void deleteUser_WhenAdminOrSameUser_UserIsDeleted() {
        User user = new User();
        user.setUsername("testUsername");
        user.setEmail("testEmail@email.com");
        user.setDeleted(false);
        User deletedBy = user;

        when(userRepository.getByUsername(user.getUsername())).thenReturn(user);

        userService.delete(user, deletedBy);

        verify(userRepository).delete(user);
    }

    @Test
    public void deleteUser_WhenUnauthorized_ThrowsException() {
        User user = new User();
        user.setUsername("testUsername");
        user.setEmail("testEmail@email.com");
        User deletedBy = new User();
        deletedBy.setUsername("testWrongUsername");
        deletedBy.setEmail("testWrongEmail@email.com");

        when(userRepository.getByUsername(user.getUsername())).thenReturn(user);

        assertThrows(UnauthorizedOperationException.class, () -> userService.delete(user, deletedBy));
    }

    @Test
    public void getUsers_WhenAdmin_ReturnsUserList() {
        User admin = new User();
        admin.setAdmin(true);
        FilterOptions filterOptions = new FilterOptions(null, null, null, null, null);
        List<User> expectedUsers = Arrays.asList(new User(), new User());

        when(userRepository.get(filterOptions)).thenReturn(expectedUsers);

        List<User> actualUsers = userService.get(filterOptions, admin);

        assertEquals(expectedUsers, actualUsers);
    }


    @Test
    public void getUsers_WhenNonAdmin_ThrowsException() {
        User nonAdmin = new User();
        FilterOptions filterOptions = new FilterOptions(null, null, null, null, null);

        assertThrows(UnauthorizedOperationException.class, () -> userService.get(filterOptions, nonAdmin));
    }

    @Test
    public void emailValidator_ValidEmail_ReturnsEmail() {
        String validEmail = "example@example.com";

        String result = userService.emailValidator(validEmail);

        assertEquals(validEmail, result);
    }

    @Test
    public void emailValidator_InvalidEmail_ThrowsException() {
        String invalidEmail = "invalidemail";

        assertThrows(InvalidEmailException.class, () -> userService.emailValidator(invalidEmail));
    }

    @Test
    public void makeAdmin_WhenUserIsNotAdmin_UserBecomesAdmin() {
        String username = "user";
        String phoneNumber = "1234567890";
        User admin = new User();
        User user = new User();
        user.setAdmin(false);

        when(userRepository.getByUsername(username)).thenReturn(user);

        userService.makeAdmin(username, phoneNumber, admin);

        assertTrue(user.isAdmin());
        verify(userRepository).addPhone(any(PhoneNumber.class));
        verify(userRepository).update(user);
    }

    @Test
    public void makeAdmin_WhenUserIsAlreadyAdmin_ThrowsException() {
        String username = "adminUser";
        User admin = new User();
        User user = new User();
        user.setAdmin(true);

        when(userRepository.getByUsername(username)).thenReturn(user);

        assertThrows(UnauthorizedOperationException.class, () -> userService.makeAdmin(username, null, admin));
    }

    @Test
    public void unmakeAdmin_WhenUserIsAdmin_UserIsNoLongerAdmin() {
        String username = "adminUser";
        User admin = new User();
        User user = new User();
        user.setAdmin(true);

        when(userRepository.getByUsername(username)).thenReturn(user);

        userService.unmakeAdmin(username, admin);

        assertFalse(user.isAdmin());
        verify(userRepository).update(user);
    }

    @Test
    public void unmakeAdmin_WhenUserIsNotAdmin_ThrowsException() {
        String username = "user";
        User admin = new User();
        User user = new User();
        user.setAdmin(false);

        when(userRepository.getByUsername(username)).thenReturn(user);

        assertThrows(UnauthorizedOperationException.class, () -> userService.unmakeAdmin(username, admin));
    }

    @Test
    public void blockUser_WhenAdmin_PerformsBlocking() {
        String username = "user";
        User admin = new User();
        admin.setAdmin(true);
        User userToBlock = new User();

        when(userRepository.getByUsername(username)).thenReturn(userToBlock);

        userService.blockUser(username, admin);

        assertTrue(userToBlock.getIsBlocked());
        verify(userRepository).update(userToBlock);
    }

    @Test
    public void blockUser_WhenNotAdmin_ThrowsException() {
        String username = "user";
        User nonAdmin = new User();
        nonAdmin.setAdmin(false);

        assertThrows(UnauthorizedOperationException.class, () -> userService.blockUser(username, nonAdmin));
    }

    @Test
    public void unblockUser_WhenAdmin_PerformsUnblocking() {
        String username = "user";
        User admin = new User();
        admin.setAdmin(true);
        User userToUnblock = new User();
        userToUnblock.setIsBlocked(true);

        when(userRepository.getByUsername(username)).thenReturn(userToUnblock);

        userService.unblockUser(username, admin);

        assertFalse(userToUnblock.getIsBlocked());
        verify(userRepository).update(userToUnblock);
    }

    @Test
    public void unblockUser_WhenNotAdmin_ThrowsException() {
        String username = "user";
        User nonAdmin = new User();
        nonAdmin.setAdmin(false);

        assertThrows(UnauthorizedOperationException.class, () -> userService.unblockUser(username, nonAdmin));
    }


}
