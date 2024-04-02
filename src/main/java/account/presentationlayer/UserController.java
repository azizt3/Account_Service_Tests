package account.presentationlayer;

import account.businesslayer.dto.UserDeletedDto;
import account.businesslayer.entity.User;
import account.businesslayer.dto.UserAdapter;
import account.businesslayer.UserService;
import account.businesslayer.dto.UpdateSuccessfulDto;
import account.businesslayer.dto.UserDto;
import account.businesslayer.request.PasswordChangeRequest;
import account.businesslayer.request.RoleChangeRequest;
import account.businesslayer.request.UserRegistrationRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@RestController
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping(path = "/api/admin/user/")
    public ResponseEntity<?> getUsers(@AuthenticationPrincipal UserAdapter user){
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(userService.handleGetUsers());
    }

    @PostMapping(path = "/api/auth/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signUp( @RequestBody UserRegistrationRequest newUser) {
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(userService.register(newUser));
    }

    @PutMapping(path = "/api/admin/user/role", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> setRoles(@RequestBody RoleChangeRequest request){
        return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(userService.handleRoleChange(request));
    }

    @PostMapping(path = "/api/auth/changepass")
    public ResponseEntity<?> changePass(
        @RequestBody PasswordChangeRequest newPassword, @AuthenticationPrincipal UserAdapter user) {
        return userService.updatePassword(newPassword.password(), user);
    }
    @DeleteMapping(path = "/api/admin/user/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable String email){
        return userService.handleUserDelete(email);
    }
}
