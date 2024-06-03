package usecase.updateuser;

import database.UserRepository;
import dto.UpdateSuccessfulDto;
import dto.UserDto;
import entity.Authority;
import entity.User;
import exceptions.ErrorMessage;
import exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utils.UserFacade;
import utils.UserHelper;

import static utils.UserHelper.getUserName;

@Service
public class UpdateUserService {

    UserRepository userRepository;
    UserHelper userHelper;
    UserFacade userFacade;

    @Autowired
    public UpdateUserService(UserRepository userRepository, UserHelper userHelper, UserFacade userFacade) {
        this.userRepository = userRepository;
        this.userHelper = userHelper;
        this.userFacade = userFacade;
    }

    public UserDto handleRoleChange(RoleChangeRequest request){
        if (!userFacade.roleExists(request.role())) throw new NotFoundException(ErrorMessage.ROLE_NOT_FOUND);
        User user = userHelper.loadUser(request.user());
        Authority authority = userFacade.getAuthorityFromRole(request.role());
        if (request.operation() == "grant") {user.addAuthority(authority);}
        if (request.operation() == "remove") user.removeAuthority(authority);
        userRepository.save(user);
        return userHelper.buildUserDto(user);
    }

    @Transactional
    public UpdateSuccessfulDto updatePassword(String newPassword) {
        userHelper.validatePasswordBreached(newPassword);
        String userName = getUserName();

        User user =  userHelper.loadUser((userName));
        user.changePassword(newPassword);
        userRepository.save(user);
        return new UpdateSuccessfulDto(user.getEmail(), "The password has been updated successfully");
    }
}
