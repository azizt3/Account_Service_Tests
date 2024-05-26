package usecase.createuser;

import database.UserRepository;
import dto.UserDto;
import entity.User;
import exceptions.ErrorMessage;
import exceptions.UserExistsException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import utils.BreachedPasswords;

import static utils.UserFacade.getDefaultAuthority;
import static utils.UserHelper.*;

@Service
public class CreateUserService {

    @Bean
    BCryptPasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(13); }
    UserRepository userRepository;
    BreachedPasswords breachedPasswords;

    @Autowired
    public CreateUserService(UserRepository userRepository, BreachedPasswords breachedPasswords) {
        this.userRepository = userRepository;
        this.breachedPasswords = breachedPasswords;
    }

    //Business Logic

    @Transactional
    public UserDto register(UserRegistrationRequest newUser) {
        validateUniqueEmail(newUser.email());
        validateNewPassword(newUser.password());
        User user = buildUser(newUser);
        userRepository.save(user);
        return buildUserDto(user);
    }

    //Validation Methods
    public void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email.toLowerCase())) {
            throw new UserExistsException(ErrorMessage.USER_EXISTS);
        }
    }

    public void validateNewPassword(String newPassword) {
        validatePasswordLength(newPassword);
        breachedPasswords.validatePasswordBreached(newPassword);
    }

    public User buildUser(UserRegistrationRequest newUser) {
        User user = new User(
            newUser.name(),
            newUser.lastname(),
            newUser.email().toLowerCase(),
            passwordEncoder().encode(newUser.password()),
            getDefaultAuthority(userRepository.count()));
        return user;
    }

    //Helper Methods
    //Any builder methods can be encapsulated into a helper/builder/factory class. Enables the

}
