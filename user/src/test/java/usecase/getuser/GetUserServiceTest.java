package usecase.getuser;

import database.UserRepository;
import dto.UserDto;
import entity.Authority;
import entity.User;
import exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.UserHelper;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetUserServiceTest {


    GetUserService getUserService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserHelper userHelper;

    @BeforeEach
    void setUp() {
        getUserService = new GetUserService(userRepository, userHelper);
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
    void givenNoExistingUsers_whenGettingUsers_thenReturnEmptyUserDtoArray() throws Exception {
        when(userRepository.count()).thenReturn(0L);
        UserDto[] expectedUsers = new UserDto[]{};
        UserDto[] users = getUserService.handleGetUsers();
        assertArrayEquals(expectedUsers, users);
    }

    @Test
    @DisplayName("getUserService -> handleGetUsers")
    void givenExistingUsers_whenGettingUsers_thenReturnUserDtoArray() throws Exception {
        UserDto[] users = {userA, userB, userC};
        when(userRepository.count()).thenReturn(3L);
        when(userHelper.buildUserDtoArray(any(List.class))).thenReturn(users);
        UserDto[] responseBody = getUserService.handleGetUsers();
        assertArrayEquals(users, responseBody);
    }

    @Test
    void givenNonExistentUser_whenFindingUser_thenThrowException() throws Exception {
        when(userRepository.findByEmail(any(String.class))).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> getUserService.handleGetUser("tabbish.aziz@acme.com"));
    }
}
