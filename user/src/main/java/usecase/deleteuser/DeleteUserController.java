package usecase.deleteuser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@RestController
public class DeleteUserController {
    @Autowired
    DeleteUserService deleteUserService;

    @DeleteMapping(path = "/api/admin/user/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable String email) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(deleteUserService.handleUserDelete(email));
    }
}
