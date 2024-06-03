package usecase.createuser;

import database.UserRepository;
import dto.UserDto;
import entity.Authority;
import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.UserHelper;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateUserServiceTest {

    CreateUserService createUserService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserHelper userHelper;

    @BeforeEach
    void setUp() {
        createUserService = new CreateUserService(userRepository, userHelper);
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


    User user1 = new User(1L, userA.name(), userA.lastname(), userA.email(), "Canada2024!!", adminAuthority);

    @Test
    void givenValidUserRegistrationRequest_whenRegisteringUser_thenReturnUserDto() throws Exception {
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
            new String[]{"ROLE_ADMINISTRATOR"}
        );

        when(userHelper.buildUser(any(UserRegistrationRequest.class))).thenReturn(user1);
        when(userRepository.save(any(User.class))).thenReturn(user1);
        when(userHelper.buildUserDto(any(User.class))).thenReturn(mockUser);
        UserDto registeredUser = createUserService.register(userRegistrationRequest);
        verify(userRepository, times(1)).save(any(User.class));
        assertEquals(mockUser, registeredUser);
    }

    @Test
    void givenInvalidUserRegistrationRequest_whenValidatingRequest_thenThrowException() throws Exception {

        UserRegistrationRequest request = new UserRegistrationRequest(
            "tabbish",
            "aziz",
            "tabbish.aziz@acme.com",
            "a");

        doThrow(new RuntimeException())
            .when(userHelper).validatePasswordLength(request.password());
        assertThrows(RuntimeException.class, () -> createUserService.register(request));
    }
}
