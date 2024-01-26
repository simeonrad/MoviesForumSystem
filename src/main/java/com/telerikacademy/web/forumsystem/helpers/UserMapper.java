package com.telerikacademy.web.forumsystem.helpers;


import com.telerikacademy.web.forumsystem.exceptions.EntityNotFoundException;
import com.telerikacademy.web.forumsystem.models.User;
import com.telerikacademy.web.forumsystem.models.UserDto;
import com.telerikacademy.web.forumsystem.repositories.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final UserRepository userRepository;

    public UserMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User fromDto(int id, UserDto userDto) {
        User user = fromDto(userDto);
        user.setId(id);
        return user;
    }

    public User fromDto(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setAdmin(false);
        user.setIsBlocked(false);
        return user;
    }


    public UserDto toDto(User newUser) {
        UserDto userDto = new UserDto();
        userDto.setUsername(newUser.getUsername());
        userDto.setFirstName(newUser.getFirstName());
        userDto.setLastName(newUser.getLastName());
        return userDto;
    }

    public User fromDtoUpdate(User userUpdated) {
        try{
            User user = userRepository.getByUsername(userUpdated.getUsername());
            user.setFirstName(userUpdated.getFirstName());
            user.setEmail(userUpdated.getEmail());
            user.setLastName(userUpdated.getLastName());
            user.setPassword(userUpdated.getPassword());
            return user;
        }
        catch (EntityNotFoundException e) {
            throw new UnsupportedOperationException("Username cannot be changed!");
        }

    }
}
