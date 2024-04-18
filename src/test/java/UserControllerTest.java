import account.AccountServiceApplication;
import account.authority.AuthorityService;
import account.businesslayer.GlobalExceptionHandler;
import account.businesslayer.UserService;
import account.businesslayer.dto.UserAdapter;
import account.businesslayer.dto.UserDto;
import account.businesslayer.entity.Authority;
import account.businesslayer.entity.User;
import account.businesslayer.exceptions.InsufficientPasswordException;
import account.businesslayer.exceptions.NotFoundException;
import account.businesslayer.exceptions.UserExistsException;
import account.businesslayer.request.RoleChangeRequest;
import account.businesslayer.request.UserRegistrationRequest;
import account.presentationlayer.UserController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


import java.util.LinkedHashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserController.class)
@Import(GlobalExceptionHandler.class)
@ContextConfiguration(classes={AccountServiceApplication.class})
@AutoConfigureMockMvc (addFilters = false)
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private AuthorityService authorityService;

    @Autowired
    private MockMvc mockMvc;

    UserDto userA = new UserDto(
        1L,
        "tabbish",
        "aziz",
        "tabbish.aziz@acme.com",
        new String[]{"ROLE_ADMINISTRATOR"});

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

    @Test
    @DisplayName("POST - api/auth/signup | Success")
    void testSignUp() throws Exception {

        UserRegistrationRequest postUser = new UserRegistrationRequest(
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

        when(userService.register(any(UserRegistrationRequest.class))).thenReturn(mockUser);

        mockMvc.perform(post("/api/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(postUser)))
            .andDo(print())

            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("tabbish")))
            .andExpect(jsonPath("$.lastname", is("aziz")))
            .andExpect(jsonPath("$.email", is("tabbish.aziz@acme.com")))
            .andExpect(jsonPath("$.roles", Matchers.contains("ROLE_ADMINISTRATOR")));
    }

    @Test
    @DisplayName("POST - api/auth/signup | Throw InsufficientPassWordException when password < 12 chars")
    void givenPasswordLessThan12Chars_whenRegisteringUser_thenThrowException() throws Exception {
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
            "tabbish",
            "aziz",
            "tabbish.aziz@acme.com",
            "toronto");

        when(userService.register(userRegistrationRequest))
            .thenThrow(new InsufficientPasswordException("Password length must be 12 chars minimum!"));

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userRegistrationRequest)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.message", is("Password length must be 12 chars minimum!")))
            .andReturn();
    }

    @Test
    @DisplayName("POST - api/auth/signup | Throw InsufficientPassWordException when password in breached list")
    void givenPasswordInBreachedList_whenRegisteringUser_thenThrowException() throws Exception {
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
            "tabbish",
            "aziz",
            "tabbish.aziz@acme.com",
            "PasswordForJanuary");

        when(userService.register(userRegistrationRequest))
            .thenThrow(new InsufficientPasswordException("This password can be guessed easily"));

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userRegistrationRequest)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.message", is("This password can be guessed easily")))
            .andReturn();
    }

    @Test
    @DisplayName("POST - api/auth/signup | Throw UserExistsException if request email is already registered")
    void givenNonUniqueEmail_whenRegisteringUser_thenThrowException() throws Exception {
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
            "tabbish",
            "aziz",
            "tabbish.aziz@acme.com",
            "PasswordForJanuary");

        when(userService.register(userRegistrationRequest))
            .thenThrow(new UserExistsException("User Exists!"));

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userRegistrationRequest)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.message", is("User Exists!")))
            .andReturn();
    }

    @Test
    @DisplayName("GET - /api/admin/user/ - Success")
    void givenAuthenticatedUser_whenGettingUsers_thenReturnUserDetails() throws Exception {
        Set<Authority> authorities = new LinkedHashSet<>();
        authorities.add(new Authority("ROLE_ADMINISTRATOR", "ADMINISTRATIVE"));

        User adminUser = new User(
            "tabbish",
            "aziz",
            "tabbish.aziz@acme.com",
            "Canada2024!", authorities);

        UserAdapter user = new UserAdapter(adminUser);

        UserDto[] allUsers = new UserDto[]{userA, userB, userC};
        when(userService.handleGetUsers()).thenReturn(allUsers);

        mockMvc.perform(get("/api/admin/user/")
            .contentType(MediaType.APPLICATION_JSON))

            .andExpect(status().isOk())
            .andDo(print())

            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id", is(userA.id().intValue())))
            .andExpect(jsonPath("$[0].name", is(userA.name())))
            .andExpect(jsonPath("$[0].lastname", is(userA.lastname())))
            .andExpect(jsonPath("$[0].email", is(userA.email())))
            .andExpect(jsonPath("$[0].roles", contains(userA.roles())))

            .andExpect(jsonPath("$[1].id", is(userB.id().intValue())))
            .andExpect(jsonPath("$[1].name", is(userB.name())))
            .andExpect(jsonPath("$[1].lastname", is(userB.lastname())))
            .andExpect(jsonPath("$[1].email", is(userB.email())))
            .andExpect(jsonPath("$[1].roles", contains(userB.roles())))

            .andExpect(jsonPath("$[2].name", is(userC.name())))
            .andExpect(jsonPath("$[2].lastname", is(userC.lastname())))
            .andExpect(jsonPath("$[2].email", is(userC.email())))
            .andExpect(jsonPath("$[2].roles", contains(userC.roles())))
            .andReturn();
    }

    @Test
    @DisplayName("GET - /api/admin/user/ | Returns empty array if no registered users")
    void givenAuthenticatedUser_whenGettingEmptyUsers_thenReturnEmptyUserArray() throws Exception {
        Set<Authority> authorities = new LinkedHashSet<>();
        authorities.add(new Authority("ROLE_ADMINISTRATOR", "ADMINISTRATIVE"));

        User adminUser = new User(
            "tabbish",
            "aziz",
            "tabbish.aziz@acme.com",
            "Canada2024!", authorities);

        UserDto[] emptyUserDtoArr = new UserDto[]{};

        when(userService.handleGetUsers()).thenReturn(emptyUserDtoArr);
        mockMvc.perform(get("/api/admin/user/")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string("[]"))
            .andReturn();
    }

    @Test
    @DisplayName("PUT - /api/admin/user/role | Returns updated user details")
    void givenRoleChangeRequest_whenChangingRoles_thenReturnUpdatedUserDetails() throws Exception {

        RoleChangeRequest roleChangeRequest = new RoleChangeRequest(
            "doffy@acme.com",
            "ACCOUNTANT",
            "GRANT"
            );

        UserDto userD = new UserDto(
            3L,
            "donquixote",
            "doflamingo",
            "doffy@acme.com",
            new String[]{"ROLE_USER", roleChangeRequest.role()}
        );

        when(userService.handleRoleChange(any(RoleChangeRequest.class))).thenReturn(userD);

        mockMvc.perform(put("/api/admin/user/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(roleChangeRequest)))
                .andDo(print())

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.id", is(userD.id().intValue())))
                .andExpect(jsonPath("$.name", is(userD.name())))
                .andExpect(jsonPath("$.lastname", is(userD.lastname())))
                .andExpect(jsonPath("$.email", is(userD.email())))
                .andExpect(jsonPath("$.roles", Matchers.contains("ROLE_USER", "ROLE_ACCOUNTANT")))
                .andReturn();
    }

    @Test
    @DisplayName("PUT - /api/admin/user/role | Throws NotFoundException when adding/removing non-existent role")
    void givenNonExistingRole_whenChangingRoles_thenThrowException() throws Exception {

        RoleChangeRequest changeRequest = new RoleChangeRequest(
                "doffy@acme.com",
                "PIRATE",
                "GRANT"
        );

        when(userService.handleRoleChange(changeRequest))
                .thenThrow(new NotFoundException("Role not found!"));

        mockMvc.perform(put("/api/admin/user/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(changeRequest)))
                .andDo(print())

                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("Role not found!")))
                .andReturn();
    }





}

















