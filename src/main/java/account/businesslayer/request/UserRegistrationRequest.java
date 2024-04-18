package account.businesslayer.request;

import account.businesslayer.response.ErrorMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(
   @JsonProperty("name") @NotBlank (message = ErrorMessage.BLANK_FIELD) String name,
   @JsonProperty("lastname") @NotBlank String lastname,
   @JsonProperty("email") @NotBlank @Email @Pattern(regexp = "^(.+)@acme.com$") String email,
   @JsonProperty("password") @NotBlank @Size(min = 12, message = ErrorMessage.PASSWORD_TOO_SHORT) String password) {
}
