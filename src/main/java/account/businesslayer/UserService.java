package account.businesslayer;

import account.BreachedPasswords;
import account.authority.AuthorityService;
import account.authority.Role;
import account.businesslayer.dto.UpdateSuccessfulDto;
import account.businesslayer.dto.UserAdapter;
import account.businesslayer.dto.UserDeletedDto;
import account.businesslayer.dto.UserDto;
import account.businesslayer.entity.Authority;
import account.businesslayer.entity.User;
import account.businesslayer.exceptions.InsufficientPasswordException;
import account.businesslayer.exceptions.InvalidChangeException;
import account.businesslayer.exceptions.NotFoundException;
import account.businesslayer.exceptions.UserExistsException;
import account.businesslayer.request.RoleChangeRequest;
import account.businesslayer.request.UserRegistrationRequest;
import account.businesslayer.response.ErrorMessage;
import account.persistencelayer.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Bean
    BCryptPasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(13); }
    UserRepository userRepository;
    AuthorityService authorityService;
    BreachedPasswords breachedPasswords;

    User userA = new User(
            "tabbish",
            "aziz",
            "tabbish.aziz@acme.com",
            "Canada2024!",
            null
    );


    @Autowired
    public UserService(UserRepository userRepository, BreachedPasswords breachedPasswords,
                       AuthorityService authorityService) {
        this.userRepository = userRepository;
        this.breachedPasswords = breachedPasswords;
        this.authorityService = authorityService;
    }

    //Business Logic

    public UserDto[] handleGetUsers(){
        if (userRepository.count() == 0) return new UserDto[]{};
        return buildUserDtoArray(userRepository.findAll());
    }

    @Transactional
    public UserDto register(UserRegistrationRequest newUser) {
        validateUniqueEmail(newUser.email());
        validateNewPassword(newUser.password());
        User user = buildUser(newUser);
        userRepository.save(user);
        return buildUserDto(user);
    }

    public UserDto handleRoleChange(RoleChangeRequest request){
        User user = loadUser(request.user());
        //authorityService.validateRoleExists(request.role());
        if (request.operation().equalsIgnoreCase("grant")) return handleRoleGrant(request, user);
        if (request.operation().equalsIgnoreCase("remove")) return handleRoleRemove(request, user);
        else throw new NotFoundException("Operation Doesn't Exist");
    }

    public UserDto handleRoleRemove(RoleChangeRequest request, User user) {
        Set<Authority> currentAuthorities = user.getAuthorities();
        authorityService.validateRoleRemoval(request, currentAuthorities);
        Set<Authority> newAuthorities = authorityService.modifyUserAuthority(currentAuthorities, request);

        user.setAuthorities(newAuthorities);
        userRepository.save(user);

        return buildUserDto(user);
    }

    /*
    * do nothing for the validate role removal (Assume it passes)8
    * return the changed authority set when calling modify user authority
    * verify save method is invoked // Valid request test # 1
    * verify userDto matches expected value// valid request test #2
    * */

    public UserDto handleRoleGrant(RoleChangeRequest request, User user) {
        Authority newAuthority = authorityService.getAuthoritybyRole(request.role());
        authorityService.validateNoRoleConflict(user.getAuthorities(), newAuthority);
        Set<Authority> newAuthorities = new LinkedHashSet<>(user.getAuthorities());
        newAuthorities.add(newAuthority);
        user.setAuthorities(newAuthorities);
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
        validateUniquePassword(newPassword, user.getPassword());
        breachedPasswords.validatePasswordBreached(newPassword);
        user.setPassword(passwordEncoder().encode(newPassword));
        userRepository.save(user);
        return new UpdateSuccessfulDto(user.getEmail(), "The password has been updated successfully");
    }

    @Transactional
    public UserDeletedDto handleUserDelete(String email) {
        User user = loadUser(email);
        if (getRoles(user).contains(Role.ADMINISTRATOR)) {
            throw new InvalidChangeException(ErrorMessage.REMOVING_ADMIN_ROLE);
        }
        userRepository.deleteByEmail(email);
        return new UserDeletedDto(email, "Deleted successfully!");
    }

    public User loadUser (String email) {
        return userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException(("")));
        return new UserAdapter(user/*, getUserAuthorities(user)*/);
    }

    private Collection<? extends GrantedAuthority> getUserAuthorities(User user) {
        return getRoles(user).stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    //Validation Methods

    public void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email.toLowerCase())) {
            throw new UserExistsException(ErrorMessage.USER_EXISTS);
        }
    }

    public void validatePasswordLength(String newPassword) {
        if (newPassword.length() < 12) {
            throw new InsufficientPasswordException(ErrorMessage.PASSWORD_TOO_SHORT);
        }
    }

    public void validateUniquePassword(String newPassword, String oldPassword) {
        if (passwordEncoder().matches(newPassword, oldPassword)) {
            throw new InsufficientPasswordException(ErrorMessage.PASSWORD_NOT_UNIQUE);
        }
    }

    public void validateNewPassword(String newPassword) {
        validatePasswordLength(newPassword);
        breachedPasswords.validatePasswordBreached(newPassword);
    }

    public void validateUserExists(String employee) {
        if (!userRepository.existsByEmail(employee.toLowerCase())){
            throw new NotFoundException(ErrorMessage.USER_NOT_FOUND);
        }
    }

    //Helper Methods

    public List<String> getRoles(User user) {
        return user.getAuthorities()
            .stream()
            .map(authority -> authority.getRole())
            .sorted()
            .toList();
    }

    //Any builder methods can be encapsulated into a helper/builder/factory class. Enables the

    private User buildUser(UserRegistrationRequest newUser) {
        User user = new User(
                newUser.name(),
                newUser.lastname(),
                newUser.email().toLowerCase(),
                passwordEncoder().encode(newUser.password()),
                authorityService.setAuthority());
        return user;
    }

    public UserDto  buildUserDto(User user) {
        return new UserDto(
            user.getId(),
            user.getName(),
            user.getLastname(),
            user.getEmail(),
            getRoles(user).toArray(new String[0]));
    }

    public UserDto[] buildUserDtoArray(List<User> users){
        return users.stream()
                .map(this::buildUserDto)
                .sorted(Comparator.comparing(UserDto::id))
                .toList()
                .toArray(new UserDto[0]);
    }
}
