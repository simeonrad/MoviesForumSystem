package com.telerikacademy.web.forumsystem.controllers.rest;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
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
    @Operation(
            summary = "Creating a user",
            description = "This method is used for creating a user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "In the request body you need to include username(min = 4, max = 50), password(min = 8, max = 50), " +
                            "firstName(min = 4, max = 32), lastName(min = 2, max = 32) and email(min = 10, max = 100)"),
            responses = {@ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = UserShow.class), mediaType = MediaType.APPLICATION_JSON_VALUE),
                    description = "Successful user creation"),
                    @ApiResponse(responseCode = "409",
                            description = "User with same username exists."),
                    @ApiResponse(responseCode = "422",
                            description = "Invalid email.")
            }
    )
    public UserShow createUser(@Valid @RequestBody UserDto userDto) {
        try {
            User user = userMapper.fromDto(userDto);
            userService.create(user);
            return userMapper.toDto(user);
        } catch (DuplicateExistsException de) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, de.getMessage());
        } catch (InvalidEmailException ie) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ie.getMessage());
        }
    }

    @PutMapping()
    @Operation(
            summary = "Updating a user",
            description = "This method is used for updating a user.",
            requestBody = @RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = UserDto.class)
                    ),
                    description = "In the request body, include username (min = 4, max = 50), password (min = 8, max = 50), " +
                            "firstName (min = 4, max = 32), lastName (min = 2, max = 32), and email (min = 10, max = 100)."
            ),
            parameters = {
                    @Parameter(name = "Authorization", description = "Basic authentication header", in = ParameterIn.HEADER, required = true, schema = @Schema(type = "string"))
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = UserShow.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            ),
                            description = "Successful user update"
                    ),
                    @ApiResponse(responseCode = "401", description = "Wrong username or password."),
                    @ApiResponse(responseCode = "404", description = "There is no such user."),
                    @ApiResponse(responseCode = "409", description = "If the email is already in use.")
            }
    )
    public UserShow updateUser(@RequestHeader HttpHeaders headers, @Valid @RequestBody User user) {
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
    @Operation(
            summary = "Deleting a user",
            description = "This method is used for deleting a user.",
            parameters = {
                    @Parameter(name = "username", description = "The username of the user you want to delete.", required = true, in = ParameterIn.PATH, schema = @Schema(type = "string"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of user"),
                    @ApiResponse(responseCode = "401", description = "Wrong username or password."),
                    @ApiResponse(responseCode = "404", description = "There is no such user.")
            }
    )
    public String deleteUser(@RequestHeader HttpHeaders headers, @RequestParam String username) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            User user = userRepository.getByUsername(username);
            userService.delete(user, currentUser);
            user = userRepository.getByUsername(user.getUsername());
            return String.format("User with username %s successfully deleted.", user.getUsername());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        }
    }

    @PutMapping("/block")
    @Operation(
            summary = "Blocking a user",
            description = "This method is used to block a user.",
            parameters = {
                    @Parameter(name = "username", description = "The username of the user you want to block.", required = true, in = ParameterIn.PATH, schema = @Schema(type = "string"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful blockage of user"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password."),
                    @ApiResponse(responseCode = "404", description = "User not found - there is no such user.")
            }
    )
    public UserShowAdmin blockUser(@RequestParam String username, @RequestHeader HttpHeaders headers) {
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
    @Operation(
            summary = "Unblocking a user",
            description = "This method is used to unblock a user.",
            parameters = {
                    @Parameter(name = "username", description = "The username of the user you want to unblock.", required = true, in = ParameterIn.PATH, schema = @Schema(type = "string"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful unblocking of user"),
                    @ApiResponse(responseCode = "401", description = "Wrong username or password."),
                    @ApiResponse(responseCode = "404", description = "There is no such user.")
            }
    )
    public UserShowAdmin unblockUser(@RequestParam String username, @RequestHeader HttpHeaders headers) {
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
    @Operation(
            summary = "Making user an admin",
            description = "This method is used to make a user an admin.",
            parameters = {
                    @Parameter(name = "username", description = "The username of the user you want to make an admin.", required = true, in = ParameterIn.PATH, schema = @Schema(type = "string"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully made user an admin"),
                    @ApiResponse(responseCode = "401", description = "Wrong username or password."),
                    @ApiResponse(responseCode = "404", description = "There is no such user.")
            }
    )
    public UserShowAdmin makeUserAdmin(@RequestParam String username,
                                       @RequestParam(required = false) String phoneNumber,
                                       @RequestHeader HttpHeaders headers) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            userService.makeAdmin(username, phoneNumber, currentUser);
            User admin = userRepository.getByUsername(username);
            return userMapper.toDtoAdmin(admin);
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/unmakeAdmin")
    @Operation(
            summary = "Unmaking user an admin",
            description = "This method is used for unmaking user an admin.",
            parameters = {
                    @Parameter(name = "username", description = "The username of the user you want to unmake an admin.", required = true, in = ParameterIn.PATH, schema = @Schema(type = "string"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful removal of admin privileges"),
                    @ApiResponse(responseCode = "401", description = "Wrong username or password."),
                    @ApiResponse(responseCode = "404", description = "There is no such user.")
            }
    )
    public UserShowAdmin unmakeUserAdmin(@RequestParam String username,
                                         @RequestHeader HttpHeaders headers) {
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
    @Operation(
            summary = "Getting a list with all the users",
            description = "This method is used for getting a list with all the users. Requires Basic Authentication.",
            parameters = {
                    @Parameter(name = "username", description = "The username of the user you want to get."),
                    @Parameter(name = "email", description = "The email of the user you want to get."),
                    @Parameter(name = "firstName", description = "The first name of the user you want to get."),
                    @Parameter(name = "sortBy", description = "The sort by method for the user you want to sort."),
                    @Parameter(name = "sortOrder", description = "The sort order for the user you want to order.")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of users"),
                    @ApiResponse(responseCode = "400", description = "Wrong given query parameters"),
                    @ApiResponse(responseCode = "401", description = "Wrong username or password."),
                    @ApiResponse(responseCode = "404", description = "There is no such user.")
            }
    )
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