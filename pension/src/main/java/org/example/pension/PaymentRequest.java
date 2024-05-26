package org.example.pension;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record PaymentRequest(
        @NotBlank
        @Email
        @JsonProperty("employee")
        String employee,
        @NotBlank
        @Pattern(regexp = "(0[1-9]|1[1,2])-(19|20)\\d{2}", message = "Invalid date!")
        @JsonProperty("period")
        String period,
        @Positive(message = "Salary cannot be negative!")
        @JsonProperty("salary")
        Long salary){}
