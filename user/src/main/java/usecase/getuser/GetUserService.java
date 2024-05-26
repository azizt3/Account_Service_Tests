package usecase.getuser;

import database.UserRepository;
import dto.UserDto;
import entity.User;
import entity.UserAdapter;
import exceptions.ErrorMessage;
import exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static utils.UserHelper.buildUserDto;
import static utils.UserHelper.buildUserDtoArray;

@Service
public class GetUserService implements UserDetailsService {

    UserRepository userRepository;

    @Autowired
    public GetUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //Business Logic
    public UserDto[] handleGetUsers(){
        if (userRepository.count() == 0) return new UserDto[]{};
        return buildUserDtoArray(userRepository.findAll());
    }

    public UserDto handleGetUser(String email){
        return buildUserDto(userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND)));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException(("")));
        return new UserAdapter(user);
    }
}
