package utils;

import database.UserRepository;
import entity.Authority;
import org.springframework.stereotype.Component;
import usecase.AuthorityService;

@Component
public class UserFacade {

    private AuthorityService authorityService;
    private UserRepository userRepository;

    public UserFacade(AuthorityService authorityService, UserRepository userRepository){
        this.authorityService = authorityService;
        this.userRepository = userRepository;
    }

    public Authority getAuthorityFromRole(String role){
        return authorityService.getAuthoritybyRole(role);
    }

    public boolean roleExists(String role){
        return authorityService.roleExists(role);
    }

    public Authority getDefaultAuthority(){
        long numberOfUsers = userRepository.count();
        return authorityService.getDefaultAuthority(numberOfUsers);
    }
}
