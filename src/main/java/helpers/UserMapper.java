package helpers;

import models.User;
import models.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

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
}
