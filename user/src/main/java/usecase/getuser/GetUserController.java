package usecase.getuser;

import entity.UserAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@RestController
public class GetUserController {
    @Autowired
    GetUserService getUserService;

    @GetMapping(path = "/api/admin/user/")
    public ResponseEntity<?> getUsers(@AuthenticationPrincipal UserAdapter user) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(getUserService.handleGetUsers());
    }

    @GetMapping(path = "api/admin/user/{email}")
    public ResponseEntity<?> getUser(@PathVariable String email){
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(getUserService.handleGetUser(email));
    }

}
