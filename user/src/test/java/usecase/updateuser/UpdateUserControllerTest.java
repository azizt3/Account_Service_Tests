package usecase.updateuser;

import dto.UserDto;
import entity.Authority;
import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateUserControllerTest {

    UpdateUserController updateUserController;

    @Mock
    UpdateUserService updateUserService;

    @Autowired
    MockMvc mockMvc;

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
    @BeforeEach
    public void setUp() { updateUserController = new UpdateUserController(updateUserService);}

    @Test
    void givenRoleGrantRequest_whenHandlingRoleUpdate_thenReturnUpdatedUserDto() throws Exception {
        UserDto updatedUserC = new UserDto(
            3L,
            "donquixote",
            "doflamingo",
            "doffy@acme.com",
            new String[]{"ROLE_USER", "ROLE_ACCOUNTANT"});

        RoleChangeRequest request = new RoleChangeRequest(userC.email(), "grant", "ROLE_ACCOUNTANT");
        when(updateUserService.handleRoleChange(request)).thenReturn(updatedUserC);
        ResponseEntity<?> response = updateUserController.setRoles(request);
        assertEquals(updatedUserC, response.getBody());
    }
}
