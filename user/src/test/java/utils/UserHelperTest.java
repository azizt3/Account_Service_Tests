package utils;

import database.UserRepository;
import dto.UserDto;
import entity.Authority;
import entity.User;
import exceptions.InsufficientPasswordException;
import exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import usecase.createuser.UserRegistrationRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserHelperTest {

    UserHelper userHelper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFacade userFacade;

    @BeforeEach
    void setUp(){
        userHelper = new UserHelper(userRepository, userFacade);
    }

    Authority admin = new Authority("ROLE_ADMINISTRATOR", "ADMINISTRATIVE");
    Authority accountant = new Authority("ROLE_ACCOUNTANT", "USER");
    Authority user = new Authority("ROLE_USER", "USER");
    Set<Authority> adminAuthority = Set.of(admin);
    Set<Authority> accountantAuthority = Set.of(accountant);
    Set<Authority> userAuthority = Set.of(user);
    Set<Authority> accountantAndUserAuthority = Set.of(user, accountant);

    //TESTS

    //userHelper.loadUser()

    @Test
    void givenValidEmail_whenFindingUsers_thenReturnUserEntity() throws Exception {

        String userEmail = "tabbish.aziz@acme.com";

        User adminUser = new User(
            1L,
            "tabbish",
            "aziz",
            "tabbish.aziz@acme.com",
            "Canada2024!!",
            adminAuthority
        );

        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(adminUser));
        User actualUser = userHelper.loadUser(userEmail);
        verify(userRepository, times(1)).findByEmail(any(String.class));
        assertEquals(adminUser, actualUser);
    }

    @Test
    void givenNonExistentUserEmail_whenFindingUsers_thenThrowNotFoundException() throws Exception{
        String email = "tabbish.aziz@acme.com";
        when(userRepository.findByEmail(any(String.class))).thenThrow(new NotFoundException(""));
        assertThrows(NotFoundException.class, () -> userHelper.loadUser(email));
        verify(userRepository, times(1)).findByEmail(any(String.class));
    }

    //userHelper.userExists()
    //Trivial method, no tests created for this

    //userHelper.buildUser()

    @Test
    void GivenFirstUserRegistration_whenBuildingUserEntity_thenReturnUserObject() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
            "tabbish",
            "aziz",
            "tabbish.aziz@acme.com",
            "Canada2024!!"
        );

        User expectedUserEntity = new User(
            "tabbish",
            "aziz",
            "tabbish.aziz@acme.com",
            "Canada2024!!",
            userAuthority
        );

        when(userRepository.count()).thenReturn(3L);
        when(userFacade.getDefaultAuthority()).thenReturn(user);
        User actualUserEntity = userHelper.buildUser(request);
        assertEquals(expectedUserEntity.getName(), actualUserEntity.getName());
        assertEquals(expectedUserEntity.getLastname(), actualUserEntity.getLastname());
        assertEquals(expectedUserEntity.getEmail(), actualUserEntity.getEmail());
        assertEquals(expectedUserEntity.getAuthorities(), actualUserEntity.getAuthorities());
    }

    @Test
    void GivenUserRegistration_whenBuildingUserEntity_thenVerifyUserRole() throws Exception{
        UserRegistrationRequest request = new UserRegistrationRequest(
            "tabbish",
            "aziz",
            "tabbish.aziz@acme.com",
            "Canada2024!!"
        );

        User expectedUserEntity = new User(
            "tabbish",
            "aziz",
            "tabbish.aziz@acme.com",
            "Canada2024!!",
            adminAuthority
        );

        when(userRepository.count()).thenReturn(0L);
        when(userFacade.getDefaultAuthority()).thenReturn(admin);
        User actualUserEntity = userHelper.buildUser(request);
        assertEquals(expectedUserEntity.getName(), actualUserEntity.getName());
        assertEquals(expectedUserEntity.getLastname(), actualUserEntity.getLastname());
        assertEquals(expectedUserEntity.getEmail(), actualUserEntity.getEmail());
        assertEquals(expectedUserEntity.getAuthorities(), actualUserEntity.getAuthorities());
    }

    //userHelper.buildUserDto()

    @Test
    void givenUserObject_whenBuildingUserDto_thenReturnUserDtoWithRoleArray() throws Exception{

        User userEntity = new User(
            "tabbish",
            "aziz",
            "tabbish.aziz@acme.com",
            "Canada2024!!",
            adminAuthority
        );

        UserDto expectedDto = new UserDto(
            1L,
            "tabbish",
            "aziz",
            "tabbish.aziz@acme.com",
            new String[]{"ROLE_ADMINISTRATOR"}
        );

        UserDto actualDto = userHelper.buildUserDto(userEntity);
        assertArrayEquals(expectedDto.roles(), actualDto.roles());
    }


    @Test
    void givenListOfUsers_whenConvertingToUserDtoArray_thenReturnUserDtoArray(){

        User adminUser = new User(
            1L,
            "tabbish",
            "aziz",
            "tabbish.aziz@acme.com",
            "Canada2024!!",
            adminAuthority
        );

        UserDto adminUserDto = new UserDto(
            1L,
            "tabbish",
            "aziz",
            "tabbish.aziz@acme.com",
            new String[]{"ROLE_ADMINISTRATOR"}
        );

        User basicUser = new User(
            2L,
            "donquixote",
            "doflamingo",
            "doffy@acme.com",
            "Dressrosa2024!!",
            userAuthority
        );

        UserDto basicUserDto = new UserDto(
            2L,
            "donquixote",
            "doflamingo",
            "doffy@acme.com",
            new String[]{"ROLE_USER"}
        );

        User accountantUser = new User(
            3L,
            "monkey",
            "d-luffy",
            "luffy@acme.com",
            "NewWorld2024!!",
            accountantAuthority
        );

        UserDto accountantUserDto = new UserDto(
            3L,
            "monkey",
            "d-luffy",
            "luffy@acme.com",
            new String[]{"ROLE_ACCOUNTANT"}
        );

        UserDto[] expectedDtoArray = new UserDto[]{adminUserDto, basicUserDto, accountantUserDto};
        UserDto[] actualDtoArray = userHelper.buildUserDtoArray(List.of(adminUser, basicUser, accountantUser));
        assertArrayEquals(expectedDtoArray, actualDtoArray);
    }

    @Test
    void givenPasswordLessThan12_whenValidatingPassLength_thenThrowInsufficientPasswordException() throws Exception{
        String password = "a";
        assertThrows(InsufficientPasswordException.class, () -> userHelper.validatePasswordLength(password));
    }

    @Test
    void givenBreachedPassword_whenDeterminingIfPasswordBreached_thenThrowException() throws Exception {
        String breachedPass = "PasswordForJanuary";
        try (MockedStatic<BreachedPasswords> breachedPassword =Mockito.mockStatic(BreachedPasswords.class)){
            breachedPassword
                .when(()->BreachedPasswords.isBreached(any(String.class)))
                .thenThrow(new InsufficientPasswordException(""));

            assertThrows(InsufficientPasswordException.class, () -> userHelper.validatePasswordBreached(breachedPass));
        };
    }

    //UserHelper.getUserName to be included in Integration tests only (requires spring security context)
}




































