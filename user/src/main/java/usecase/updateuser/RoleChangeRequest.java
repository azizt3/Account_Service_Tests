package usecase.updateuser;

import com.fasterxml.jackson.annotation.JsonProperty;
import exceptions.ErrorMessage;
import jakarta.validation.constraints.NotBlank;

public record RoleChangeRequest(
    @JsonProperty("user") @NotBlank (message = ErrorMessage.BLANK_FIELD) String user,
    @JsonProperty("role") @NotBlank (message = ErrorMessage.BLANK_FIELD) String role,
    @JsonProperty("operation") @NotBlank (message = ErrorMessage.BLANK_FIELD) String operation) {

    public RoleChangeRequest(String user, String role, String operation){
        this.user = user;
        this.operation=operation;
        this.role = role;
    }

    }



