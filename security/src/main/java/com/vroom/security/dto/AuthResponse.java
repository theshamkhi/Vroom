package com.vroom.security.dto;

import com.vroom.security.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Response DTO for authentication operations
 * Contains JWT tokens and user information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;

    // User information
    private UUID userId;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private boolean emailVerified;
}