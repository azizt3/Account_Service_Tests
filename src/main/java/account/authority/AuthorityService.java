package account.authority;

import account.exceptionhandling.InvalidChangeException;
import account.exceptionhandling.NotFoundException;
import account.exceptionhandling.ErrorMessage;
import account.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthorityService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    AuthorityRepository authorityRepository;

    public Set<Authority> setAuthority(){
        return userRepository.count() > 0 ?
                Set.of(getAuthoritybyRole(Role.USER)):Set.of(getAuthoritybyRole(Role.ADMINISTRATOR));
    }

    public Authority getAuthoritybyRole(String role){
        return authorityRepository.findByRole(role)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.ROLE_NOT_FOUND));
    }

    public Set<Authority> modifyUserAuthority (Set<Authority> currentAuthority, RoleChangeRequest request) {
        Authority authorityToDelete = getAuthoritybyRole(request.role());
        return currentAuthority.stream()
                .filter(authority -> !authority.equals(authorityToDelete))
                .collect(Collectors.toSet());
    }

    public void validateNoRoleConflict(Set<Authority> currentAuthorities, Authority newAuthority) {
        for (Authority authority : currentAuthorities) {
            if (!authority.getRoleGroup().equalsIgnoreCase(newAuthority.getRoleGroup()))
                throw new InvalidChangeException(ErrorMessage.CONFLICTING_ROLE_ASSIGNMENT);
        }
    }

    public void validateRoleExists(String role) {
        if (!authorityRepository.existsByRole(role)) {
            throw new NotFoundException(ErrorMessage.ROLE_NOT_FOUND);
        }
    }

    public void validateRoleRemoval(RoleChangeRequest request, Set<Authority> currentAuthorities) {
        if (request.role().equalsIgnoreCase(Role.ADMINISTRATOR)) {
            throw new InvalidChangeException(ErrorMessage.REMOVING_ADMIN_ROLE);
        }
        if (!currentAuthorities.contains(getAuthoritybyRole(request.role()))) {
            throw new InvalidChangeException(ErrorMessage.REMOVING_UNASSIGNED_ROLE);
        }
        if (currentAuthorities.size() < 2) {
            throw new InvalidChangeException(ErrorMessage.REMOVING_ONLY_ROLE);
        }
    }
}
