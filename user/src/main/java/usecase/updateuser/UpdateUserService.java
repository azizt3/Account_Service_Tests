package usecase.updateuser;

import database.UserRepository;
import dto.UpdateSuccessfulDto;
import dto.UserDto;
import entity.User;
import exceptions.ErrorMessage;
import exceptions.InsufficientPasswordException;
import exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import utils.BreachedPasswords;

import static utils.UserFacade.roleExists;
import static utils.UserHelper.*;

@Service
public class UpdateUserService {

    UserRepository userRepository;

    BreachedPasswords breachedPasswords;
    @Bean
    BCryptPasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(13); }

    @Autowired
    public UpdateUserService(UserRepository userRepository, BreachedPasswords breachedPasswords) {
        this.userRepository = userRepository;
        this.breachedPasswords = breachedPasswords;
    }

    public UserDto handleRoleChange(RoleChangeRequest request){
        if (!roleExists(request.role())) throw new NotFoundException(ErrorMessage.ROLE_NOT_FOUND);
        User user = loadUser(request.user());
        if (request.operation().equalsIgnoreCase("grant")) user.addAuthority(request.role());
        if (request.operation().equalsIgnoreCase("remove")) user.removeAuthority(request.role());
        else throw new NotFoundException("Operation Doesn't Exist");
        userRepository.save(user);
        return buildUserDto(user);
    }

    @Transactional
    public UpdateSuccessfulDto updatePassword(String newPassword) {
        String userName = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user =  loadUser((userName));
        validatePasswordLength(newPassword);
        if (passwordEncoder().matches(newPassword, user.getPassword())) {
            throw new InsufficientPasswordException(ErrorMessage.PASSWORD_NOT_UNIQUE);
        }
        breachedPasswords.validatePasswordBreached(newPassword);
        user.setPassword(passwordEncoder().encode(newPassword));
        userRepository.save(user);
        return new UpdateSuccessfulDto(user.getEmail(), "The password has been updated successfully");
    }


}
