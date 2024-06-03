package usecase.getuser;

import dto.UserDto;
import entity.Authority;
import entity.User;
import entity.UserAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetUserControllerTest {

    GetUserController getUserController;

    @Mock
    GetUserService getUserService;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    public void setUp() { getUserController = new GetUserController(getUserService);}

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
    void givenAuthenticatedUser_whenGettingUsers_thenReturnEmptyUserArray() throws Exception {
        UserDto[] expectedBody = new UserDto[]{};
        when(getUserService.handleGetUsers()).thenReturn(new UserDto[]{});
        ResponseEntity<?> users = getUserController.getUsers(new UserAdapter(user1));
        assertArrayEquals(expectedBody, (Object[]) users.getBody());
    }

    @Test
    void givenValidUserEmail_whenGettingUser_thenReturnUserDto() throws Exception {
        when(getUserService.handleGetUser("tabbish.aziz@acme.com")).thenReturn(userA);
        UserDto expectedUser = userA;
        ResponseEntity<?> response = getUserController.getUser("tabbish.aziz@acme.com");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUser, response.getBody());
    }

    @Test
    void givenNonExistentUserEmail_whenFindingUser_thenThrowException() throws Exception {
        when(getUserService.handleGetUser("tabbish.aziz@acme.com")).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> getUserController.getUser("tabbish.aziz@acme.com"));
    }
}
