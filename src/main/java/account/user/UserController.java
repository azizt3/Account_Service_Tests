package account.user;

import account.authority.RoleChangeRequest;
import account.user.request.PasswordChangeRequest;
import account.user.request.UserRegistrationRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@RestController
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping(path = "/api/admin/user/")
    public ResponseEntity<?> getUsers(@AuthenticationPrincipal UserAdapter user) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.handleGetUsers());
    }

    @PostMapping(path = "/api/auth/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signUp(@RequestBody @Valid UserRegistrationRequest newUser) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.register(newUser));
    }

    @PutMapping(path = "/api/admin/user/role", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> setRoles(@RequestBody @Valid RoleChangeRequest request) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.handleRoleChange(request));
    }

    @PostMapping(path = "/api/auth/changepass")
    public ResponseEntity<?> changePass(
        @RequestBody PasswordChangeRequest newPassword, @AuthenticationPrincipal UserAdapter user) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.updatePassword(newPassword.password()));
    }

    @DeleteMapping(path = "/api/admin/user/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable String email) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.handleUserDelete(email));
    }
}
