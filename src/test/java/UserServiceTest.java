import account.BreachedPasswords;
import account.authority.AuthorityService;
import account.businesslayer.UserService;
import account.businesslayer.dto.UserDto;
import account.businesslayer.entity.Authority;
import account.businesslayer.entity.User;
import account.businesslayer.exceptions.InsufficientPasswordException;
import account.businesslayer.exceptions.NotFoundException;
import account.businesslayer.exceptions.UserExistsException;
import account.businesslayer.request.RoleChangeRequest;
import account.businesslayer.request.UserRegistrationRequest;
import account.persistencelayer.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.relation.Role;
import java.sql.Array;
import java.util.*;


import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthorityService authorityService;

    @Mock
    private BreachedPasswords breachedPasswords;

    @BeforeEach
    void setUp(){
        userService = new UserService(userRepository, breachedPasswords, authorityService);
    }

    Authority admin = new Authority("ROLE_ADMINISTRATOR", "ADMINISTRATIVE");
    Authority accountant = new Authority("ROLE_ACCOUNTANT", "USER");
    Authority user = new Authority("ROLE_USER", "USER");
    Set<Authority> adminAuthority = Set.of(admin);
    Set<Authority> accountantAuthority = Set.of(accountant);
    Set<Authority> userAuthority = Set.of(user);
    Set<Authority> accountantAndUserAuthority = Set.of(user, accountant);

    UserDto userA = new UserDto(
            1L,
            "tabbish",
            "aziz",
            "tabbish.aziz@acme.com",
            new String[]{admin.getRole()});

    UserDto userB = new UserDto(
            2L,
            "Monkey",
            "D-Luffy",
            "luffy@acme.com",
            new String[]{"ROLE_ACCOUNTANT"}
    );

    UserDto userC = new UserDto(
            3L,
            "donquixote",
            "doflamingo",
            "doffy@acme.com",
            new String[]{"ROLE_USER"}
    );

    User user1 = new User(1L, userA.name(), userA.lastname(), userA.email(), "Canada2024!!", adminAuthority);
    User user2 = new User(2L, userB.name(), userB.lastname(), userC.email(), "Laughtale2024!!", accountantAuthority );
    User user3 = new User(3L, userC.name(), userC.lastname(), userC.email(), "Dressrosa2024!!", userAuthority);

    @Test
    void givenUserDetails_whenRegisteringUsers_thenCallRepositorySave(){

        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                "tabbish",
                "aziz",
                "tabbish.aziz@acme.com",
                "Canada2024!!");

        UserDto mockUser = new UserDto(
                1L,
                "tabbish",
                "aziz",
                "tabbish.aziz@acme.com",
                new String[]{"ROLE_ADMINISTRATOR"});

        when(userRepository.save(any(User.class))).thenReturn(new User());

        userService.register(userRegistrationRequest);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void givenUserDetails_whenRegisteringUsers_thenSetsUserAuthority(){

        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                "tabbish",
                "aziz",
                "tabbish.aziz@acme.com",
                "Canada2024!!");

        UserDto mockUser = new UserDto(
                1L,
                "tabbish",
                "aziz",
                "tabbish.aziz@acme.com",
                new String[]{"ROLE_ADMINISTRATOR"});

        when(userRepository.save(any(User.class))).thenReturn(new User());

        UserDto registeredUser = userService.register(userRegistrationRequest);

        verify(authorityService, times(1)).setAuthority();
    }

    @Test
    void givenUserDetails_whenRegisteringUsers_thenReturnUserDto(){

        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                "tabbish",
                "aziz",
                "tabbish.aziz@acme.com",
                "Canada2024!!");

        Authority authority = new Authority("ROLE_ADMINISTRATOR", "ADMINISTRATIVE");
        Set<Authority> authorities = Set.of(authority);

        User mockUser = new User (
                userRegistrationRequest.name(),
                userRegistrationRequest.lastname(),
                userRegistrationRequest.email(),
                userRegistrationRequest.password(),
                authorities
        );

        /*UserDto mockUserDto = new UserDto(
                1L,
                "tabbish",
                "aziz",
                "tabbish.aziz@acme.com",
                new String[]{"ROLE_ADMINISTRATOR"});*/

        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        UserDto mockUserDto = userService.register(userRegistrationRequest);
        assertNotNull(mockUserDto);
        assertEquals("tabbish", mockUserDto.name());
        assertEquals("aziz", mockUserDto.lastname());
        assertEquals("tabbish.aziz@acme.com", mockUserDto.email());
        assertNotNull(mockUserDto.roles());
    }

    @Test
    void givenUserDetailsWithShortPass_whenRegisteringUsers_thenThrowException(){

        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                "tabbish",
                "aziz",
                "tabbish.aziz@acme.com",
                "tabbishaziz");

        assertThrows(InsufficientPasswordException.class, () -> userService.register(userRegistrationRequest));
    }

    @Test
    void givenUserDetailsWithExistingEmail_whenRegisteringUsers_thenThrowException(){

        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                "tabbish",
                "aziz",
                "tabbish.aziz@acme.com",
                "Canada2024!!");

        when(userRepository.existsByEmail(userRegistrationRequest.email())).thenReturn(true);
        assertThrows(UserExistsException.class, () -> userService.register(userRegistrationRequest));
    }

    @Test
    void givenUserDetailsWithBreachedPassword_whenRegisteringUsers_thenThrowException(){

        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                "tabbish",
                "aziz",
                "tabbish.aziz@acme.com",
                "PasswordForApril");

        doThrow(new InsufficientPasswordException("The password is in the hacker's database!"))
                .when(breachedPasswords).validatePasswordBreached(userRegistrationRequest.password());

        assertThrows(InsufficientPasswordException.class, () -> userService.register(userRegistrationRequest));
    }

    @Test
    void givenAuthenticatedUserDetails_whenGettingUsers_thenReturnUserDtoArray(){

        User user1 = new User(1L, userA.name(), userA.lastname(), userA.email(), "Canada2024!!", adminAuthority);
        User user2 = new User(2L, userB.name(), userB.lastname(), userC.email(), "Laughtale2024!!", accountantAuthority );
        User user3 = new User(3L, userC.name(), userC.lastname(), userC.email(), "Dressrosa2024!!", userAuthority);

        when(userRepository.count()).thenReturn(3L);
        when(userRepository.findAll()).thenReturn(List.of(user1,user2,user3));
        UserDto[] users = userService.handleGetUsers();

        //Implement assert equals for index k:v pair in the UserDto array
        assertNotNull(users);
    }

    @Test
    void givenAuthenticatedUserDetails_whenGettingEmptyUsers_thenReturnEmptyUserDetails(){
        UserDto[] expected = new UserDto[]{};
        when(userRepository.count()).thenReturn(0L);
        UserDto[] users = userService.handleGetUsers();
        assertArrayEquals(expected, users);
    }

    @Test
    void givenRoleChangeRequestWithInvalidOperation_whenChangingRoles_thenThrowException(){
        RoleChangeRequest request = new RoleChangeRequest(
                "doffy@acme.com",
                "ACCOUNTANT",
                "Add");

        User testUser = new User(
                3L,
                "donquixote",
                "doflamingo",
                "doffy@acme.com",
                "Dressrosa2024!!",
                Set.of(user, accountant)
        );

        when(userRepository.findByEmail(request.user())).thenReturn(Optional.of(testUser));
        doNothing().when(authorityService).validateRoleExists(request.role());
        assertThrows(NotFoundException.class, () -> userService.handleRoleChange(request));
    }

    @Test
    void givenRoleChangeRequestWithNonExistentUser_whenChangingRoles_thenThrowException(){
        RoleChangeRequest request = new RoleChangeRequest(
                "bron@acme.com",
                "ACCOUNTANT",
                "GRANT");

        User testUser = new User(
                3L,
                "LeBron",
                "James",
                "bron@acme.com",
                "Cleveland2024!!",
                Set.of(admin)
        );
        assertThrows(NotFoundException.class, () -> userService.loadUser(request.user()));
    }

    @Test
    void givenRoleChangeRequestWithNonExistentRole_whenChangingRoles_thenThrowException(){
        RoleChangeRequest request = new RoleChangeRequest(
                "doffy@acme.com",
                "ENGINEER",
                "GRANT");

        User testUser = new User(
                3L,
                "donquixote",
                "doflamingo",
                "doffy@acme.com",
                "Dressrosa2024!!",
                Set.of(user, accountant)
        );

        when(userRepository.findByEmail(request.user())).thenReturn(Optional.of(testUser));
        doThrow(new NotFoundException("Role not found!")).when(authorityService).validateRoleExists(request.role());
        assertThrows(NotFoundException.class, () -> userService.handleRoleChange(request));
    }

    @Test
    void givenRoleGrantRequest_whenChangingRoles_thenVerifyUserRepositorySave(){
        validRoleGrantRequestStubbing();
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void givenRoleGrantRequest_whenChangingRoles_thenVerifyAuthorityRepositoryCheck(){
        validRoleGrantRequestStubbing();
        verify(authorityService, times(1)).validateRoleExists(any(String.class));
    }

    @Test
    void givenRoleGrantRequest_whenChangingRoles_thenReturnModifiedUserDto(){
        RoleChangeRequest request = new RoleChangeRequest(
                "doffy@acme.com",
                "ACCOUNTANT",
                "GRANT");

        Set<Authority> testUserAuthority = new HashSet<>();
        testUserAuthority.add(user);

        Set<Authority> modifiedUserAuthority = new HashSet<>();
        modifiedUserAuthority.add(user);
        modifiedUserAuthority.add(accountant);

        User testUser = new User(
                3L,
                "donquixote",
                "doflamingo",
                "doffy@acme.com",
                "Dressrosa2024!!",
                testUserAuthority
        );

        User modifiedUser = new User (
                3L,
                "donquixote",
                "doflamingo",
                "doffy@acme.com",
                "Dressrosa2024!!",
                modifiedUserAuthority
        );

        when(userRepository.findByEmail(request.user())).thenReturn(Optional.of(testUser));
        doNothing().when(authorityService).validateRoleExists(request.role());
        when(authorityService.getAuthoritybyRole(request.role())).thenReturn(accountant);
        when(userRepository.save(any(User.class))).thenReturn(modifiedUser);
        UserDto finalUser = userService.handleRoleChange(request);
        assertEquals(modifiedUser.getId(), finalUser.id());
        assertEquals(modifiedUser.getName(), finalUser.name());
        assertEquals(modifiedUser.getLastname(), finalUser.lastname());
        assertEquals(modifiedUser.getEmail(), finalUser.email());
        assertArrayEquals(new String[]{"ROLE_ACCOUNTANT", "ROLE_USER"}, finalUser.roles());
    }

    @Test
    void givenRoleRemovalRequest_whenChangingRoles_thenVerifyUserRepositorySave(){
        validRoleRemovalRequestStubbing();
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void givenRoleRemovalRequest_whenChangingRoles_thenModifiedUserDto(){
        RoleChangeRequest request = new RoleChangeRequest(
                "doffy@acme.com",
                "ACCOUNTANT",
                "REMOVE");

        Set<Authority> testUserAuthority = new HashSet<>();
        testUserAuthority.add(accountant);
        testUserAuthority.add(user);

        Set<Authority> modifiedUserAuthority = new HashSet<>(2);
        modifiedUserAuthority.add(user);

        User testUser = new User(
                3L,
                "donquixote",
                "doflamingo",
                "doffy@acme.com",
                "Dressrosa2024!!",
                testUserAuthority
        );

        User modifiedUser = new User (
                3L,
                "donquixote",
                "doflamingo",
                "doffy@acme.com",
                "Dressrosa2024!!",
                modifiedUserAuthority
        );

        doNothing().when(authorityService).validateRoleRemoval(request, testUser.getAuthorities());
        when(authorityService.modifyUserAuthority(testUserAuthority, request)).thenReturn(modifiedUserAuthority);
        when(userRepository.save(any(User.class))).thenReturn(modifiedUser);
        UserDto finalUser = userService.handleRoleRemove(request, testUser);
        assertEquals(modifiedUser.getId(), finalUser.id());
        assertEquals(modifiedUser.getName(), finalUser.name());
        assertEquals(modifiedUser.getLastname(), finalUser.lastname());
        assertEquals(modifiedUser.getEmail(), finalUser.email());
        assertArrayEquals(new String[]{"ROLE_USER"}, finalUser.roles());
    }

    private void validRoleGrantRequestStubbing() {
        RoleChangeRequest request = new RoleChangeRequest(
                "doffy@acme.com",
                "ACCOUNTANT",
                "GRANT");

        Authority accountantAuthority = new Authority("ROLE_ACCOUNTANT", "USER");
        Set<Authority> testUserAuthority = new HashSet<>();
        testUserAuthority.add(accountantAuthority);

        Set<Authority> modifiedUserAuthority = new HashSet<>(2);
        modifiedUserAuthority.add(user);
        modifiedUserAuthority.add(accountant);

        User testUser = new User(
                3L,
                "donquixote",
                "doflamingo",
                "doffy@acme.com",
                "Dressrosa2024!!",
                testUserAuthority
        );

        User modifiedUser = new User (
                3L,
                "donquixote",
                "doflamingo",
                "doffy@acme.com",
                "Dressrosa2024!!",
                modifiedUserAuthority
        );

        when(userRepository.findByEmail(request.user())).thenReturn(Optional.of(testUser));
        doNothing().when(authorityService).validateRoleExists(request.role());
        when(authorityService.getAuthoritybyRole(request.role())).thenReturn(accountantAuthority);
        when(userRepository.save(any(User.class))).thenReturn(modifiedUser);
        UserDto finalUser = userService.handleRoleChange(request);
    }

    private void validRoleRemovalRequestStubbing(){
        RoleChangeRequest request = new RoleChangeRequest(
                "doffy@acme.com",
                "ACCOUNTANT",
                "REMOVE");

        Set<Authority> testUserAuthority = new HashSet<>();
        testUserAuthority.add(accountant);
        testUserAuthority.add(user);

        Set<Authority> modifiedUserAuthority = new HashSet<>(2);
        modifiedUserAuthority.add(user);

        User testUser = new User(
                3L,
                "donquixote",
                "doflamingo",
                "doffy@acme.com",
                "Dressrosa2024!!",
                testUserAuthority
        );

        User modifiedUser = new User (
                3L,
                "donquixote",
                "doflamingo",
                "doffy@acme.com",
                "Dressrosa2024!!",
                modifiedUserAuthority
        );

        doNothing().when(authorityService).validateRoleRemoval(request, testUser.getAuthorities());
        when(authorityService.modifyUserAuthority(testUserAuthority, request)).thenReturn(modifiedUserAuthority);
        when(userRepository.save(any(User.class))).thenReturn(modifiedUser);
        UserDto finalUser = userService.handleRoleRemove(request, testUser);
    }


}