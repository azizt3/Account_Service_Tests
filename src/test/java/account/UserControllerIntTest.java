package account;

import account.authority.Role;
import account.businesslayer.dto.UserDto;
import account.businesslayer.entity.Authority;
import account.businesslayer.entity.User;
import account.businesslayer.request.PasswordChangeRequest;
import account.businesslayer.request.RoleChangeRequest;
import account.businesslayer.request.UserRegistrationRequest;
import account.businesslayer.response.ErrorMessage;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//Scans for container annotations

/*
TAKEAWAYS
- Look into hexagonal architechture
- makes applications scalable.

*/

@Testcontainers
//Load the application context, but use a random port as the web environment.
//Spins up a Tomcat embedded server
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class UserControllerIntTest {

    private static final String API_AUTH_CHANGEPASS = "/api/auth/changepass";
    private static final String API_ADMIN_USER_ROLE = "/api/admin/user/role";
    private static final String API_AUTH_SIGNUP = "/api/auth/signup";
    private static final String API_ADMIN_USER = "/api/admin/user/";
    private static final String API_ADMIN_USER_EMAIL = "/api/admin/user/{email}";
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postGres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    //Authentication & Authorization

    @Test
    void apiAdminUsers_shouldRespondWithHttp401() throws Exception {

        mockMvc.perform(get(API_ADMIN_USER)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "doffy@acme.com", roles = {Role.USER})
    void apiAdminUsers_shouldRespondWithHttp403() throws Exception {

        mockMvc.perform(get(API_ADMIN_USER)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    void apiAdminUserRole_shouldRespondWith401() throws Exception {
        RoleChangeRequest request = new RoleChangeRequest(
            "userA@acme,com",
            Role.ACCOUNTANT,
            "GRANT");

        mockMvc.perform(put(API_ADMIN_USER_ROLE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isUnauthorized());

    }

    @WithMockUser(username = "doffy@acme.com", roles = {Role.USER})
    @Test
    void apiAdminUserRole_shouldRespondWith403() throws Exception {
        RoleChangeRequest request = new RoleChangeRequest(
            "userA@acme,com",
            Role.ACCOUNTANT,
            "GRANT");

        mockMvc.perform(put(API_ADMIN_USER_ROLE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    void apiAuthChangepass_noAuth_shouldReturnHttp401() throws Exception {

        PasswordChangeRequest newPassword = new PasswordChangeRequest("newPassword!@#$");

        mockMvc.perform(post(API_AUTH_CHANGEPASS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(newPassword)))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    void apiAdminUser_noAuth_shouldReturnHttp401() throws Exception {

        mockMvc.perform(delete(API_ADMIN_USER_EMAIL, "tabbish.aziz@acme.com")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "doffy@acme.com", roles = {Role.USER})
    @Test
    void apiAdminUser_userRole_shouldReturnHttp403() throws Exception {
        mockMvc.perform(delete(API_ADMIN_USER_EMAIL, "tabbish.aziz@acme.com")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isForbidden());
    }


    @WithMockUser (username = "luffy@acme.com", roles = {Role.ACCOUNTANT})
    @Test
    void apiAdminUser_accountantRole_shouldReturnHttp403() throws Exception {
        mockMvc.perform(delete(API_ADMIN_USER_EMAIL, "tabbish.aziz@acme.com")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @WithMockUser (username = "someotheruser@acme.com", roles = {Role.USER, Role.ACCOUNTANT})
    @Test
    void apiAdminUser_userAndAccountantRole_shouldReturnHttp403() throws Exception {
        mockMvc.perform(delete(API_ADMIN_USER_EMAIL, "tabbish.aziz@acme.com")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    //Invalid Request Body

    @Test
    void apiAuthSignup_notBlankFieldIsBlank_shouldRespondWithHttp400() throws Exception {

        UserRegistrationRequest request = new UserRegistrationRequest(
            "",
            "james",
            "lebronjames@acme.com",
            "Brampton2024!!"
        );

        mockMvc.perform(post(API_AUTH_SIGNUP)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.error", is("Bad Request")))
            .andExpect(jsonPath("$.message", is(ErrorMessage.BLANK_FIELD)))
            .andExpect(jsonPath("$.path", is(API_AUTH_SIGNUP)));
    }

    @Test
    void apiAuthSignup_withExistingEmail_shouldRespondWithHttp400() throws Exception {

        UserRegistrationRequest request = new UserRegistrationRequest(
            "tabbish",
            "aziz",
            "tabbish.aziz@acme.com",
            "Canada2024!!"
        );

        mockMvc.perform(post(API_AUTH_SIGNUP)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.error", is("Bad Request")))
            .andExpect(jsonPath("$.message", is(ErrorMessage.USER_EXISTS)))
            .andExpect(jsonPath("$.path", is(API_AUTH_SIGNUP)));
    }

    @Test
    void apiAuthSignup_withShortPassword_shouldRespondWithHttp400() throws Exception {

        UserRegistrationRequest request = new UserRegistrationRequest(
            "ash",
            "ketchum",
            "ash.ketchum@acme.com",
            "pallettown"
        );

        mockMvc.perform(post(API_AUTH_SIGNUP)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.error", is("Bad Request")))
            .andExpect(jsonPath("$.message", is(ErrorMessage.PASSWORD_TOO_SHORT)))
            .andExpect(jsonPath("$.path", is(API_AUTH_SIGNUP)));
    }

    @Test
    void apiAuthSignup_withBreachedPassword_shouldRespondWithHttp400() throws Exception {

        UserRegistrationRequest request = new UserRegistrationRequest(
            "kobe",
            "bryant",
            "kobe@acme.com",
            "PasswordForJanuary"
        );

        mockMvc.perform(post(API_AUTH_SIGNUP)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.error", is("Bad Request")))
            .andExpect(jsonPath("$.message", is(ErrorMessage.BREACHED_PASSWORD)))
            .andExpect(jsonPath("$.path", is(API_AUTH_SIGNUP)));
    }

    @WithMockUser(username = "tabbish.aziz@acme.com", roles = Role.ADMINISTRATOR)
    @Test
    void apiAdminUser_usersTableEmpty_shouldReturnEmptyBody(){}

    @WithMockUser(username = "tabbish.aziz@acme.com", roles = Role.ADMINISTRATOR)
    @Test
    void apiAdminUserRole_nonExistentUser_shouldReturnHttp404() throws Exception{

        RoleChangeRequest request = new RoleChangeRequest(
            "nonExistentUser@acme.com",
            Role.ACCOUNTANT,
            "GRANT");

        mockMvc.perform(put(API_ADMIN_USER_ROLE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andDo(print())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Not Found")))
            .andExpect(jsonPath("$.message", is(ErrorMessage.USER_NOT_FOUND)))
            .andExpect(jsonPath("$.path", is(API_ADMIN_USER_ROLE)));
    }

    @WithMockUser(username = "tabbish.aziz@acme.com", roles = Role.ADMINISTRATOR)
    @Test
    void apiAdminUserRole_nonExistentRole_shouldReturnHttp404() throws Exception{
        RoleChangeRequest request = new RoleChangeRequest(
            "luffy@acme.com",
            "PROJECT_MANAGER",
            "GRANT");

        mockMvc.perform(put(API_ADMIN_USER_ROLE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andDo(print())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Not Found")))
            .andExpect(jsonPath("$.message", is(ErrorMessage.ROLE_NOT_FOUND)))
            .andExpect(jsonPath("$.path", is(API_ADMIN_USER_ROLE)));

    }

    @WithMockUser(username = "tabbish.aziz@acme.com", roles = Role.ADMINISTRATOR)
    @Test
    void apiAdminUserRole_blankFields_shouldReturnHttp400() throws Exception {
        RoleChangeRequest request = new RoleChangeRequest(
            "",
            Role.ACCOUNTANT,
            "GRANT");

        mockMvc.perform(put(API_ADMIN_USER_ROLE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andDo(print())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.error", is("Bad Request")))
            .andExpect(jsonPath("$.message", is(ErrorMessage.BLANK_FIELD)))
            .andExpect(jsonPath("$.path", is(API_ADMIN_USER_ROLE)));
    }

    @WithMockUser(username = "tabbish.aziz@acme.com", roles = Role.ADMINISTRATOR)
    @Test
    void apiAdminUserRole_withConflictingRoles_shouldReturnHttp400() throws Exception {
        RoleChangeRequest request = new RoleChangeRequest(
            "luffy@acme.com",
            Role.ADMINISTRATOR,
            "GRANT");

        mockMvc.perform(put(API_ADMIN_USER_ROLE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andDo(print())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.error", is(HttpStatus.BAD_REQUEST.getReasonPhrase())))
            .andExpect(jsonPath("$.message", is(ErrorMessage.CONFLICTING_ROLE_ASSIGNMENT)))
            .andExpect(jsonPath("$.path", is(API_ADMIN_USER_ROLE)));

    }

    @WithMockUser(username = "tabbish.aziz@acme.com", roles = Role.ADMINISTRATOR)
    @Test
    void apiAdminUserRole_removingAdminRole_shouldReturnHttp400() throws Exception {
        RoleChangeRequest request = new RoleChangeRequest(
            "tabbish.aziz@acme.com",
            Role.ADMINISTRATOR,
            "REMOVE");

        mockMvc.perform(put(API_ADMIN_USER_ROLE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andDo(print())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.error", is(HttpStatus.BAD_REQUEST.getReasonPhrase())))
            .andExpect(jsonPath("$.message", is(ErrorMessage.REMOVING_ADMIN_ROLE)))
            .andExpect(jsonPath("$.path", is(API_ADMIN_USER_ROLE)));
    }

    @WithMockUser(username = "tabbish.aziz@acme.com", roles = Role.ADMINISTRATOR)
    @Test
    void apiAdminUserRole_removingUnAssignedRole_shouldReturnHttp400() throws Exception {
        RoleChangeRequest request = new RoleChangeRequest(
            "doffy@acme.com",
            Role.ACCOUNTANT,
            "REMOVE");

        mockMvc.perform(put(API_ADMIN_USER_ROLE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andDo(print())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.error", is(HttpStatus.BAD_REQUEST.getReasonPhrase())))
            .andExpect(jsonPath("$.message", is(ErrorMessage.REMOVING_UNASSIGNED_ROLE)))
            .andExpect(jsonPath("$.path", is(API_ADMIN_USER_ROLE)));

    }

    @WithMockUser(username = "tabbish.aziz@acme.com", roles = Role.ADMINISTRATOR)
    @Test
    void apiAdminUserRole_removingOnlyRole_shouldReturnHttp400() throws Exception {
        RoleChangeRequest request = new RoleChangeRequest(
            "luffy@acme.com",
            Role.ACCOUNTANT,
            "REMOVE");

        mockMvc.perform(put(API_ADMIN_USER_ROLE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andDo(print())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.error", is(HttpStatus.BAD_REQUEST.getReasonPhrase())))
            .andExpect(jsonPath("$.message", is(ErrorMessage.REMOVING_ONLY_ROLE)))
            .andExpect(jsonPath("$.path", is(API_ADMIN_USER_ROLE)));
    }

    @WithMockUser(username = "tabbish.aziz@acme.com", roles = Role.ADMINISTRATOR)
    @Test
    void apiAuthChangePass_sameAsOldPassword_returnHttp400() throws Exception {
        PasswordChangeRequest request = new PasswordChangeRequest("Canada2024!!");

        mockMvc.perform(post(API_AUTH_CHANGEPASS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andDo(print())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.error", is(HttpStatus.BAD_REQUEST.getReasonPhrase())))
            .andExpect(jsonPath("$.message", is(ErrorMessage.PASSWORD_NOT_UNIQUE)))
            .andExpect(jsonPath("$.path", is(API_AUTH_CHANGEPASS)));
    }

    @WithMockUser(username = "tabbish.aziz@acme.com", roles = Role.ADMINISTRATOR)
    @Test
    void apiAuthChangePass_passwordInBreachedList_returnHttp400() throws Exception {

        PasswordChangeRequest request = new PasswordChangeRequest("PasswordForFebruary");

        mockMvc.perform(post(API_AUTH_CHANGEPASS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andDo(print())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.error", is(HttpStatus.BAD_REQUEST.getReasonPhrase())))
            .andExpect(jsonPath("$.message", is(ErrorMessage.BREACHED_PASSWORD)))
            .andExpect(jsonPath("$.path", is(API_AUTH_CHANGEPASS)));

    }

    //Does this even need to be a test case??
    @WithMockUser(username = "NonExistentUser@acme.com", roles = Role.ADMINISTRATOR)
    @Test
    void apiAuthChangePass_nonExistentUser_returnHttp400() throws Exception {
        PasswordChangeRequest request = new PasswordChangeRequest("Canada2024!!");

        mockMvc.perform(post(API_AUTH_CHANGEPASS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andDo(print())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is(HttpStatus.NOT_FOUND.getReasonPhrase())))
            .andExpect(jsonPath("$.message", is(ErrorMessage.USER_NOT_FOUND)))
            .andExpect(jsonPath("$.path", is(API_AUTH_CHANGEPASS)));
    }

    @WithMockUser(username = "tabbish.aziz@acme.com", roles = Role.ADMINISTRATOR)
    @Test
    @Rollback
    void setApiAdminUserEmail_removingAdmin_returnHttp400() throws Exception {
        mockMvc.perform(delete(API_ADMIN_USER_EMAIL, "tabbish.aziz@acme.com")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.error", is(HttpStatus.BAD_REQUEST.getReasonPhrase())))
            .andExpect(jsonPath("$.message", is(ErrorMessage.REMOVING_ADMIN_ROLE)))
            .andExpect(jsonPath("$.path", is("/api/admin/user/tabbish.aziz@acme.com")));
    }

    @WithMockUser(username = "tabbish.aziz@acme.com", roles = Role.ADMINISTRATOR)
    @Test
    void setApiAdminUserEmail_removingNonExistentUser_returnHttp400() throws Exception {
        mockMvc.perform(delete(API_ADMIN_USER_EMAIL, "nonExistentUser@acme.com")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is(HttpStatus.NOT_FOUND.getReasonPhrase())))
            .andExpect(jsonPath("$.message", is(ErrorMessage.USER_NOT_FOUND)))
            .andExpect(jsonPath("$.path", is("/api/admin/user/nonExistentUser@acme.com")));
    }


    //Valid Request Objects

    @Test
    @WithMockUser(username = "tabbish.aziz@acme.com", roles = {Role.ADMINISTRATOR})
    @Rollback
    void apiAdminUser_shouldRespondWithHttp200() throws Exception {

        mockMvc.perform(get(API_ADMIN_USER)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(print());
    }

    @Test
    @Rollback
    void apiAuthSignUp_shouldRespondWithHttp200() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
            "pinunder",
            "jeep",
            "pinunder@acme.com",
            "Brampton2024!!"
        );

        mockMvc.perform(post(API_AUTH_SIGNUP)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @WithMockUser(username = "tabbish.aziz@acme.com", roles={Role.ADMINISTRATOR})
    @Test
    @Rollback
    void apiAdminUserRole_shouldRespondWith200() throws Exception {
        RoleChangeRequest request = new RoleChangeRequest(
            "doffy@acme.com",
            Role.ACCOUNTANT,
            "GRANT");

        mockMvc.perform(put(API_ADMIN_USER_ROLE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @Rollback
    @WithMockUser(username = "tabbish.aziz@acme.com", roles={Role.ADMINISTRATOR})
    void setApiAuthChangepass_shouldRespondWith200() throws Exception {
        PasswordChangeRequest request = new PasswordChangeRequest("008nw6gc5xr7");

        mockMvc.perform(post(API_AUTH_CHANGEPASS)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @Rollback
    @WithMockUser(username = "tabbish.aziz@acme.com", roles={Role.ADMINISTRATOR})
    void setApiAdminUserEmail_shouldRespondWith200() throws Exception {
        PasswordChangeRequest request = new PasswordChangeRequest("008nw6gc5xr7");

        mockMvc.perform(delete(API_ADMIN_USER_EMAIL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(print());
    }



    /*
    Valid Responses for all controller methods
    */

    /*
    Payment Repository -- test all repository methods
    */






}












/*ResponseEntity<UserDto[]> users = restTemplate.withBasicAuth(
                "tabbish.aziz@acme.com",
                "Canada2024!!")
                .getForEntity("/api/admin/user/", UserDto[].class);*/

        /*UserDto[] usersArray = users.getBody();
        System.out.println(usersArray.toString());
        assertEquals(users.getStatusCode(), HttpStatus.OK);
        assertThat(usersArray.length).isEqualTo(3);*/