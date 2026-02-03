package com.vroom.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for email verification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationDTO {

    @NotBlank(message = "Verification token is required")
    private String token;

    private String email;
    private boolean verified;
    private String message;
}