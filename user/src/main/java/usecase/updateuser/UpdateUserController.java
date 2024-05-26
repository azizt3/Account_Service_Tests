package usecase.updateuser;

import entity.UserAdapter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@RestController
public class UpdateUserController {
    @Autowired
    UpdateUserService updateUserService;

    @PutMapping(path = "/api/admin/user/role", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> setRoles(@RequestBody @Valid RoleChangeRequest request) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateUserService.handleRoleChange(request));
    }

    @PostMapping(path = "/api/auth/changepass")
    public ResponseEntity<?> changePass(
        @RequestBody PasswordChangeRequest newPassword, @AuthenticationPrincipal UserAdapter user) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateUserService.updatePassword(newPassword.password()));
    }
}
