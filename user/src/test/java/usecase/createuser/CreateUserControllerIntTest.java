package usecase.createuser;


import exceptions.ErrorMessage;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
//Load the application context, but use a random port as the web environment.
//Spins up a Tomcat embedded server
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = CreateUserControllerTest.class)
@Transactional
public class CreateUserControllerIntTest {

    private static final String API_AUTH_SIGNUP = "/api/auth/signup";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postGres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

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
}
