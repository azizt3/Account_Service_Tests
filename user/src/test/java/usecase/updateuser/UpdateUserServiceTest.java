package usecase.updateuser;

import database.UserRepository;
import dto.UserDto;
import entity.Authority;
import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.UserFacade;
import utils.UserHelper;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class UpdateUserServiceTest {

    UpdateUserService updateUserService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserHelper userHelper;

    @Mock
    private UserFacade userFacade;


    Authority admin = new Authority("ROLE_ADMINISTRATOR", "ADMINISTRATIVE");
    Authority accountant = new Authority("ROLE_ACCOUNTANT", "USER");
    Authority user = new Authority("ROLE_USER", "USER");
    Set<Authority> adminAuthority = new LinkedHashSet<>(Arrays.asList(admin));
    //Set.of(admin);
    Set<Authority> accountantAuthority = new LinkedHashSet<>(Arrays.asList(accountant));
    Set<Authority> userAuthority = new LinkedHashSet<>(Arrays.asList(user));
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

    @BeforeEach
    void setUp() {
        updateUserService = new UpdateUserService(userRepository, userHelper, userFacade);
    }

    /*@Test
    void roleGrantTest() throws Exception{
        RoleChangeRequest request = new RoleChangeRequest(userC.email(), "ROLE_ACCOUNTANT", "grant");
        User user3Modified = new User(3L, userC.name(), userC.lastname(), userC.email(), user3.getPassword(), accountantAndUserAuthority);

        when(userFacade.roleExists(request.role())).thenReturn(true);
        when(userHelper.loadUser(request.user())).thenReturn(user3);
        when(userFacade.getAuthorityFromRole(request.role())).thenReturn(accountant);
        //doNothing().when(any(User.class)).addAuthority(any(Authority.class));
        when(userRepository.save(any(User.class))).thenReturn(user3Modified);
        UserDto updatedUser = updateUserService.handleRoleChange(request);
        UserDto expectedUpdatedUser = new UserDto(
            3L,
            userC.name(),
            userC.lastname(),
            userC.email(),
            new String[]{"ROLE_USER", "ROLE_ACCOUNTANT"}
        );
        assertEquals(expectedUpdatedUser, updatedUser);
    }*/
}
