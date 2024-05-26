package dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentPostedDto(@NotBlank String status, String pensionBalance) {


}
