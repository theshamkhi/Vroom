package com.vroom.security.service;

import com.vroom.notification.service.EmailService;
import com.vroom.security.dto.*;
import com.vroom.security.jwt.JwtService;
import com.vroom.security.model.entity.Instructor;
import com.vroom.security.model.entity.Student;
import com.vroom.security.model.entity.User;
import com.vroom.security.model.enums.Role;
import com.vroom.security.repository.InstructorRepository;
import com.vroom.security.repository.StudentRepository;
import com.vroom.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service handling authentication operations
 * Manages user registration, login, password reset, and email verification
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    /**
     * Register a new user
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Check if user already exists
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Validate instructor-specific requirements
        if (request.getRole() == Role.INSTRUCTOR && request.getLicenseNumber() != null) {
            if (instructorRepository.existsByLicenseNumber(request.getLicenseNumber())) {
                throw new IllegalArgumentException("License number already registered");
            }
        }

        // Create user based on role
        User user = createUserByRole(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(false); // Require email verification

        // Generate email verification token
        String verificationToken = UUID.randomUUID().toString();
        user.setEmailVerificationToken(verificationToken);
        user.setEmailVerificationTokenExpiry(LocalDateTime.now().plusHours(24));

        // Save user
        User savedUser = saveUserByRole(user, request.getRole());

        // Send verification email
        try {
            emailService.sendVerificationEmail(
                    savedUser.getEmail(),
                    savedUser.getFullName(),
                    verificationToken
            );
            log.info("Verification email sent to: {}", savedUser.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", savedUser.getEmail(), e);
            // Don't fail registration if email sending fails
        }

        log.info("User registered successfully: {}", savedUser.getEmail());

        // Generate tokens
        String accessToken = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        return buildAuthResponse(savedUser, accessToken, refreshToken);
    }

    /**
     * Authenticate user and generate tokens
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        // Authenticate
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Load user
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if email is verified
        if (!user.isEnabled()) {
            throw new IllegalStateException("Email not verified. Please verify your email before logging in.");
        }

        // Update last login
        user.updateLastLogin();
        userRepository.save(user);

        // Generate tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        log.info("User logged in successfully: {}", user.getEmail());

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    /**
     * Refresh access token
     */
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        final String refreshToken = request.getRefreshToken();
        final String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail == null) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        User user = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return buildAuthResponse(user, newAccessToken, newRefreshToken);
    }

    /**
     * Verify email with token
     */
    @Transactional
    public EmailVerificationDTO verifyEmail(String token) {
        log.info("Verifying email with token");

        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));

        // Check token expiry
        if (user.getEmailVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification token has expired");
        }

        // Enable user account
        user.setEnabled(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiry(null);
        userRepository.save(user);

        // Send welcome email
        try {
            emailService.sendWelcomeEmail(
                    user.getEmail(),
                    user.getFullName(),
                    user.getRole().toString()
            );
            log.info("Welcome email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", user.getEmail(), e);
            // Don't fail verification if welcome email fails
        }

        log.info("Email verified successfully for: {}", user.getEmail());

        return EmailVerificationDTO.builder()
                .email(user.getEmail())
                .verified(true)
                .message("Email verified successfully. Welcome to Vroom!")
                .build();
    }

    /**
     * Request password reset
     */
    @Transactional
    public void requestPasswordReset(String email) {
        log.info("Password reset requested for: {}", email);

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Generate reset token
        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        // Send reset email
        try {
            emailService.sendPasswordResetEmail(
                    user.getEmail(),
                    user.getFullName(),
                    resetToken
            );
            log.info("Password reset email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", email, e);
            throw new RuntimeException("Failed to send password reset email. Please try again later.");
        }
    }

    /**
     * Reset password with token
     */
    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        log.info("Resetting password for: {}", request.getEmail());

        User user = userRepository.findByPasswordResetToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid reset token"));

        // Verify email matches
        if (!user.getEmail().equalsIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email does not match reset token");
        }

        // Check token expiry
        if (user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reset token has expired. Please request a new one.");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);

        log.info("Password reset successfully for: {}", user.getEmail());

        // Optionally send confirmation email
        try {
            emailService.sendSimpleEmail(
                    user.getEmail(),
                    "Password Changed Successfully",
                    String.format("Hi %s,\n\nYour password has been successfully changed. " +
                            "If you did not make this change, please contact support immediately.\n\n" +
                            "Best regards,\nVroom Team", user.getFullName())
            );
        } catch (Exception e) {
            log.error("Failed to send password change confirmation email", e);
        }
    }

    /**
     * Resend verification email
     */
    @Transactional
    public void resendVerificationEmail(String email) {
        log.info("Resending verification email to: {}", email);

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.isEnabled()) {
            throw new IllegalStateException("Email already verified");
        }

        // Generate new verification token
        String verificationToken = UUID.randomUUID().toString();
        user.setEmailVerificationToken(verificationToken);
        user.setEmailVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        // Send verification email
        try {
            emailService.sendVerificationEmail(
                    user.getEmail(),
                    user.getFullName(),
                    verificationToken
            );
            log.info("Verification email resent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to resend verification email to: {}", email, e);
            throw new RuntimeException("Failed to send verification email. Please try again later.");
        }
    }

    // Helper methods

    private User createUserByRole(RegisterRequest request) {
        return switch (request.getRole()) {
            case STUDENT -> Student.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .phoneNumber(request.getPhoneNumber())
                    .role(Role.STUDENT)
                    .dateOfBirth(request.getDateOfBirth())
                    .drivingSchool(request.getDrivingSchool())
                    .permitNumber(request.getPermitNumber())
                    .preferredLanguage(request.getPreferredLanguage() != null ? request.getPreferredLanguage() : "en")
                    .build();

            case INSTRUCTOR -> Instructor.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .phoneNumber(request.getPhoneNumber())
                    .role(Role.INSTRUCTOR)
                    .licenseNumber(request.getLicenseNumber())
                    .drivingSchool(request.getDrivingSchool())
                    .specialty(request.getSpecialty())
                    .yearsOfExperience(request.getYearsOfExperience())
                    .bio(request.getBio())
                    .certifications(request.getCertifications())
                    .build();

            case ADMIN -> User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .phoneNumber(request.getPhoneNumber())
                    .role(Role.ADMIN)
                    .build();
        };
    }

    private User saveUserByRole(User user, Role role) {
        return switch (role) {
            case STUDENT -> studentRepository.save((Student) user);
            case INSTRUCTOR -> instructorRepository.save((Instructor) user);
            case ADMIN -> userRepository.save(user);
        };
    }

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .emailVerified(user.isEnabled())
                .build();
    }
}