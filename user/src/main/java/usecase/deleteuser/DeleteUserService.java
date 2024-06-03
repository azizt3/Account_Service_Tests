package usecase.deleteuser;

import database.UserRepository;
import dto.UserDeletedDto;
import entity.User;
import exceptions.ErrorMessage;
import exceptions.InvalidChangeException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utils.UserHelper;

@Service
public class DeleteUserService {

    UserRepository userRepository;
    UserHelper userHelper;

    @Autowired
    public DeleteUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserDeletedDto handleUserDelete(String email) {
        User user = userHelper.loadUser(email);
        if (user.getRoles().contains("ADMINISTRATOR")) {
            throw new InvalidChangeException(ErrorMessage.REMOVING_ADMIN_ROLE);
        }
        userRepository.deleteByEmail(email);
        return new UserDeletedDto(email, "Deleted successfully!");
    }
}
