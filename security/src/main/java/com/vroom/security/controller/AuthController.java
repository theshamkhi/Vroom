package com.vroom.security.controller;

import com.vroom.security.dto.*;
import com.vroom.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for authentication operations
 * Handles registration, login, password reset, and email verification
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user
     */
    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Create a new user account (Student, Instructor, or Admin)")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration request received for email: {}", request.getEmail());
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login user
     */
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and receive JWT tokens")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for email: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Refresh access token
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Get a new access token using refresh token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Token refresh request received");
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Verify email with token
     */
    @GetMapping("/verify-email")
    @Operation(summary = "Verify email", description = "Verify user email address with token")
    public ResponseEntity<EmailVerificationDTO> verifyEmail(@RequestParam String token) {
        log.info("Email verification request received");
        EmailVerificationDTO response = authService.verifyEmail(token);
        return ResponseEntity.ok(response);
    }

    /**
     * Resend verification email
     */
    @PostMapping("/resend-verification")
    @Operation(summary = "Resend verification email", description = "Resend email verification link")
    public ResponseEntity<Map<String, String>> resendVerification(@RequestParam String email) {
        log.info("Resend verification request for: {}", email);
        authService.resendVerificationEmail(email);
        return ResponseEntity.ok(Map.of("message", "Verification email sent successfully"));
    }

    /**
     * Request password reset
     */
    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset", description = "Send password reset link to email")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestParam String email) {
        log.info("Password reset request for: {}", email);
        authService.requestPasswordReset(email);
        return ResponseEntity.ok(Map.of("message", "Password reset email sent successfully"));
    }

    /**
     * Reset password with token
     */
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset password using reset token")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        log.info("Password reset confirmation for: {}", request.getEmail());
        authService.resetPassword(request);
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

    /**
     * Logout (client-side token invalidation)
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Logout user (client should discard tokens)")
    public ResponseEntity<Map<String, String>> logout() {
        // JWT is stateless, so logout is handled client-side by discarding the token
        log.info("Logout request received");
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}