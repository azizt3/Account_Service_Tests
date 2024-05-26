package utils;

import database.UserRepository;
import dto.UserDto;
import entity.User;
import exceptions.ErrorMessage;
import exceptions.InsufficientPasswordException;
import exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class UserHelper {

    private static UserRepository userRepository;

    @Autowired
    public UserHelper (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static User loadUser (String email) {
        return userRepository.findByEmail(email.toLowerCase())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
    }

    public static boolean userExists(String email){
        return userRepository.existsByEmail(email);
    }

    public static UserDto buildUserDto(User user) {
        return new UserDto(
            user.getId(),
            user.getName(),
            user.getLastname(),
            user.getEmail(),
            user.getRoles().toArray(new String[0]));
    }

    public static UserDto[] buildUserDtoArray(List<User> users){
        return users.stream()
            .map(user -> buildUserDto(user))
            .sorted(Comparator.comparing(UserDto::id))
            .toList()
            .toArray(new UserDto[0]);
    }

    public static void validatePasswordLength(String newPassword) {
        if (newPassword.length() < 12) {
            throw new InsufficientPasswordException(ErrorMessage.PASSWORD_TOO_SHORT);
        }
    }
}
