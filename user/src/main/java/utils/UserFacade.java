package utils;

import entity.Authority;
import org.springframework.stereotype.Component;
import usecase.AuthorityService;

import java.util.Set;

@Component
public class UserFacade {

    private static AuthorityService authorityService;

    public static Authority getAuthorityFromRole(String role){
        return authorityService.getAuthoritybyRole(role);
    }

    public static boolean roleExists(String role){
        return authorityService.roleExists(role);
    }

    public static Set<Authority> getDefaultAuthority(long numberOfUsers){
        return authorityService.getDefaultAuthority(numberOfUsers);
    }
}
