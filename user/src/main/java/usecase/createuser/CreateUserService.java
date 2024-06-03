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
import utils.UserHelper;

@Service
public class CreateUserService {

    @Bean
    BCryptPasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(13); }
    UserRepository userRepository;

    UserHelper userHelper;

    @Autowired
    public CreateUserService(UserRepository userRepository, UserHelper userHelper) {
        this.userRepository = userRepository;
        this.userHelper = userHelper;

    }

    //Business Logic

    @Transactional
    public UserDto register(UserRegistrationRequest newUser) {
        validateUniqueEmail(newUser.email());
        userHelper.validatePasswordLength(newUser.password());
        userHelper.validatePasswordBreached(newUser.password());
        User user = userHelper.buildUser(newUser);
        userRepository.save(user);
        return userHelper.buildUserDto(user);
    }

    //Validation Methods
    private void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email.toLowerCase())) {
            throw new UserExistsException(ErrorMessage.USER_EXISTS);
        }
    }

    //MOVE THIS TO USER HELPER CLASS SO IT IS EASY TO TEST


}
