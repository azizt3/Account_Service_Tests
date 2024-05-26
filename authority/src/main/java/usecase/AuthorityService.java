package usecase;

import database.AuthorityRepository;
import dto.Role;
import entity.Authority;
import exceptions.ErrorMessage;
import exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthorityService {
    
    @Autowired
    AuthorityRepository authorityRepository;

    public Set<Authority> getDefaultAuthority(long numberOfusers){
        return numberOfusers > 0 ?
                Set.of(getAuthoritybyRole(Role.USER)):Set.of(getAuthoritybyRole(Role.ADMINISTRATOR));
    }

    public Authority getAuthoritybyRole(String role){
        return authorityRepository.findByRole(role)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.ROLE_NOT_FOUND));
    }

    public boolean roleExists(String role) {
        return authorityRepository.existsByRole(role);
    }
    
}
