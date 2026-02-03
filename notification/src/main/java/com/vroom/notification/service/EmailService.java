package com.vroom.notification.service;

import java.time.LocalDateTime;

/**
 * Interface for email service operations
 */
public interface EmailService {

    /**
     * Send email verification email
     */
    void sendVerificationEmail(String to, String userName, String verificationToken);

    /**
     * Send welcome email after successful registration
     */
    void sendWelcomeEmail(String to, String userName, String userRole);

    /**
     * Send password reset email
     */
    void sendPasswordResetEmail(String to, String userName, String resetToken);

    /**
     * Send plain text email
     */
    void sendSimpleEmail(String to, String subject, String text);

    /**
     * Send assignment notification email
     */
    void sendAssignmentNotification(String to, String studentName, String assignmentTitle,
                                    String instructorName, LocalDateTime dueDate);

    /**
     * Send badge earned notification
     */
    void sendBadgeEarnedEmail(String to, String studentName, String badgeName, String badgeDescription);
}