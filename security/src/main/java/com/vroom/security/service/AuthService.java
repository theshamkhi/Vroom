package com.vroom.security.service;

import com.vroom.security.dto.*;

/**
 *   Authentication service contract
 *   Handles registration, login, token refresh,
 *   email verification, and password management.
 */
public interface AuthService {

    /**
     * Register a new user
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticate user and generate tokens
     */
    AuthResponse login(LoginRequest request);

    /**
     * Refresh access token using refresh token
     */
    AuthResponse refreshToken(RefreshTokenRequest request);

    /**
     * Verify user email using verification token
     */
    EmailVerificationDTO verifyEmail(String token);

    /**
     * Request a password reset email
     */
    void requestPasswordReset(String email);

    /**
     * Reset password using reset token
     */
    void resetPassword(PasswordResetRequest request);

    /**
     * Resend email verification link
     */
    void resendVerificationEmail(String email);
}