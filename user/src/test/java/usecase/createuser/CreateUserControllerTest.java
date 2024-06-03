package usecase.createuser;

import dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateUserControllerTest {

    CreateUserController createUserController;
    @Mock
    CreateUserService createUserService;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(){
        createUserController = new CreateUserController(createUserService);
    }



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
            new String[]{"ROLE_ADMINISTRATOR"}
        );

        when(createUserService.register(postUser)).thenReturn(mockUser);
        ResponseEntity<?> response = createUserController.signUp(postUser);
        System.out.println(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUser, response.getBody());
    }

    @Test
    @DisplayName("POST - api/auth/signup | Error" )
    void whenGivenInvalidRequest_whenValidatingRequest_thenThrowException() throws Exception {
        UserRegistrationRequest postUser = new UserRegistrationRequest(
            "tabbish",
            "",
            "tabbish.aziz@acme.com",
            "Canada2024!!");

        when(createUserService.register(postUser)).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> createUserController.signUp(postUser));
    }



}




















