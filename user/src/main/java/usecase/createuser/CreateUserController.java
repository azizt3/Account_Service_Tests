package usecase.createuser;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@RestController
public class CreateUserController {
    @Autowired
    CreateUserService createUserService;

    @PostMapping(path = "/api/auth/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signUp(@RequestBody @Valid UserRegistrationRequest newUser) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createUserService.register(newUser));
    }
}
