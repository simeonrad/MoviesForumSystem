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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    private User user;

    private User deletedUser;

    private FilterOptions filterOptions;
    private List<User> userList;
    private Page<User> expectedPage;
    private String username = "testUser";
    private String email = "user@example.com";


    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        UserMapper userMapper = mock(UserMapper.class);
        userService = new UserServiceImpl(userRepository, userMapper);
        user = new User();
        user.setId(1);
        user.setUsername("testUser");
        user.setEmail("test@user.com");
        user.setAdmin(false);
        user.setDeleted(false);
        userList = new ArrayList<>();
        userList.add(new User());
        expectedPage = new PageImpl<>(userList);
        deletedUser = new User();
        deletedUser.setUsername(username);
        deletedUser.setEmail(email);
        deletedUser.setDeleted(true);
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

    @Test
    public void addPhoneNumber_ToUser_SavesPhoneNumber() {
        String phoneNumber = "123456789";
        userService.addPhoneNumber(phoneNumber, user);

        verify(userRepository).addPhone(any(PhoneNumber.class));
    }

    @Test
    public void createUser_WhenUsernameExistsAndNotDeleted_ThrowsException() {
        User existingUser = new User();
        existingUser.setDeleted(false);
        User newUser = new User();
        newUser.setUsername("username");

        when(userRepository.getByUsername(newUser.getUsername())).thenReturn(existingUser);

        assertThrows(DuplicateExistsException.class, () -> userService.create(newUser));
    }

    @Test
    public void getUserByEmail_WhenUserExists_ReturnsUser() {
        // Arrange
        String email = "user@example.com";
        User expectedUser = new User();
        expectedUser.setEmail(email);
        when(userRepository.getByEmail(email)).thenReturn(expectedUser);

        // Act
        User actualUser = userService.get(email);

        // Assert
        assertNotNull(actualUser);
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
        verify(userRepository).getByEmail(email);
    }

    @Test
    public void getUserByEmail_WhenUserDoesNotExist_ThrowsEntityNotFoundException() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.getByEmail(email)).thenThrow(new EntityNotFoundException("User", "email", email));

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.get(email));
        verify(userRepository).getByEmail(email);
    }



    @Test
    public void addProfilePhoto_ToUser_UpdatesUserWithPhotoUrl() {
        String photoUrl = "http://example.com/photo.jpg";
        userService.addProfilePhoto(photoUrl, user);

        assertEquals(photoUrl, user.getProfilePhotoUrl());
        verify(userRepository).update(user);
    }

    @Test
    public void getAllUsers_ReturnsListOfAllUsers() {
        List<User> expectedUsers = Collections.singletonList(user);
        when(userRepository.getAll()).thenReturn(expectedUsers);

        List<User> actualUsers = userService.getAll();

        assertEquals(expectedUsers, actualUsers);
        verify(userRepository).getAll();
    }

    @Test
    public void update_WithValidEmail_UpdatesUser() {
        when(userRepository.updateEmail(user.getEmail())).thenReturn(false);
        userService.update(user);
        verify(userRepository).update(user);
    }

    @Test
    public void update_WithInvalidEmail_ThrowsInvalidEmailException() {
        User invalidEmailUser = new User();
        invalidEmailUser.setEmail("invalidemail");
        assertThrows(InvalidEmailException.class, () -> userService.update(invalidEmailUser));
    }

    @Test
    public void update_WithDuplicateEmail_ThrowsDuplicateExistsException() {
        when(userRepository.updateEmail(user.getEmail())).thenReturn(true);
        assertThrows(DuplicateExistsException.class, () -> userService.update(user));
    }


    @Test
    public void delete_UserNotDeleted_MarksUserAsDeleted() {
        assertFalse(user.isDeleted());

        userService.delete(user);

        assertTrue(user.isDeleted());
        verify(userRepository).delete(user);
    }

    @Test
    public void delete_UserAlreadyDeleted_ThrowsEntityNotFoundException() {
        user.setDeleted(true);

        assertThrows(EntityNotFoundException.class, () -> userService.delete(user));

        verify(userRepository, never()).delete(user);
    }

    @Test
    public void deleteUser_ByAdmin_DeletesUser() {
        User admin = new User();
        admin.setAdmin(true);
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@user.com");
        user.setDeleted(false);

        when(userRepository.getByUsername(user.getUsername())).thenReturn(user);
        doAnswer(invocation -> {
            User argUser = invocation.getArgument(0);
            argUser.setDeleted(true);
            return null;
        }).when(userRepository).delete(any(User.class));

        userService.delete(user, admin);

        assertTrue(user.isDeleted(), "User should be marked as deleted");
        verify(userRepository).delete(user);
    }

    @Test
    public void deleteUser_WhenAlreadyDeleted_ThrowsEntityNotFoundException() {
        User user = new User();
        user.setUsername("existingUser");
        user.setDeleted(true);

        when(userRepository.getByUsername(user.getUsername())).thenReturn(user);

        assertThrows(EntityNotFoundException.class, () -> userService.delete(user, user));
    }


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
    public void get_WithValidPagination_ReturnsPageOfUsers() {
        int page = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);

        when(userRepository.get(filterOptions, pageable)).thenReturn(expectedPage);

        Page<User> actualPage = userService.get(filterOptions, page, size);

        assertEquals(expectedPage.getTotalElements(), actualPage.getTotalElements());
        assertEquals(expectedPage.getContent(), actualPage.getContent());
    }

    @Test
    public void makeAdmin_WithValidUsername_UpdatesUserToAdmin() {
        when(userRepository.getByUsername(username)).thenReturn(user);
        userService.makeAdmin(username);
        verify(userRepository, times(1)).getByUsername(username);
        assert(user.isAdmin());
        verify(userRepository, times(1)).update(user);
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
