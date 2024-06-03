package dto;

import jakarta.validation.constraints.NotBlank;

public record UserDto(
    @NotBlank Long id,
    @NotBlank String name,
    @NotBlank String lastname,
    @NotBlank String email,
    @NotBlank String[] roles) {

    @Override
    public boolean equals(Object o) {
        UserDto dto = (UserDto)o;
        if (this.id.equals(dto.id)){
            return true;
        }
        return false;
    }
}
