package utils;

import database.UserRepository;
import dto.UserDto;
import entity.User;
import exceptions.ErrorMessage;
import exceptions.InsufficientPasswordException;
import exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import usecase.createuser.UserRegistrationRequest;

import java.util.Comparator;
import java.util.List;

import static utils.BreachedPasswords.isBreached;

@Component
public class UserHelper {

    UserRepository userRepository;
    UserFacade userFacade;

    @Autowired
    public UserHelper (UserRepository userRepository, UserFacade userFacade) {
        this.userRepository = userRepository;
        this.userFacade = userFacade;
    }

    public User loadUser (String email) {
        return userRepository.findByEmail(email.toLowerCase())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
    }

    public boolean userExists(String email){
        return userRepository.existsByEmail(email);
    }

    public User buildUser(UserRegistrationRequest newUser) {

        User user = new User(
            newUser.name(),
            newUser.lastname(),
            newUser.email().toLowerCase(),
            new BCryptPasswordEncoder().encode(newUser.password()),
            userFacade.getDefaultAuthority()
        );

        return user;
    }

    public UserDto buildUserDto(User user) {
        return new UserDto(
            user.getId(),
            user.getName(),
            user.getLastname(),
            user.getEmail(),
            user.getRoles().toArray(new String[0]));
    }

    public UserDto[] buildUserDtoArray(List<User> users){
        return users.stream()
            .map(user -> buildUserDto(user))
            .sorted(Comparator.comparing(UserDto::id))
            .toList()
            .toArray(new UserDto[0]);
    }

    public void validatePasswordLength(String newPassword) {
        if (newPassword.length() < 12) {
            throw new InsufficientPasswordException(ErrorMessage.PASSWORD_TOO_SHORT);
        }
    }

    public void validatePasswordBreached(String newPassword){
        if(isBreached(newPassword)){
            throw new InsufficientPasswordException(ErrorMessage.BREACHED_PASSWORD);
        };
    }

    public static String getUserName(){
        return SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();
    }
}
