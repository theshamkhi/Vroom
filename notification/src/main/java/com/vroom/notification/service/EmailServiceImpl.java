package com.vroom.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for sending emails
 * Supports both plain text and HTML emails with Thymeleaf templates
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.name:Vroom}")
    private String appName;

    @Value("${app.url:http://localhost:4200}")
    private String appUrl;

    /**
     * Send email verification email
     */
    @Async
    @Override
    public void sendVerificationEmail(String to, String userName, String verificationToken) {
        log.info("Sending verification email to: {}", to);

        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", userName);
            variables.put("verificationUrl", appUrl + "/verify-email?token=" + verificationToken);
            variables.put("appName", appName);
            variables.put("year", LocalDateTime.now().getYear());

            String subject = "Verify Your Email - " + appName;
            String htmlContent = templateEngine.process("email/email-verification", createContext(variables));

            sendHtmlEmail(to, subject, htmlContent);
            log.info("Verification email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", to, e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    /**
     * Send password reset email
     */
    @Async
    @Override
    public void sendPasswordResetEmail(String to, String userName, String resetToken) {
        log.info("Sending password reset email to: {}", to);

        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", userName);
            variables.put("resetUrl", appUrl + "/reset-password?token=" + resetToken);
            variables.put("appName", appName);
            variables.put("year", LocalDateTime.now().getYear());
            variables.put("expiryTime", "1 hour");

            String subject = "Reset Your Password - " + appName;
            String htmlContent = templateEngine.process("email/password-reset", createContext(variables));

            sendHtmlEmail(to, subject, htmlContent);
            log.info("Password reset email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", to, e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    /**
     * Send welcome email after successful registration
     */
    @Async
    @Override
    public void sendWelcomeEmail(String to, String userName, String userRole) {
        log.info("Sending welcome email to: {}", to);

        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", userName);
            variables.put("userRole", userRole);
            variables.put("loginUrl", appUrl + "/login");
            variables.put("appName", appName);
            variables.put("year", LocalDateTime.now().getYear());

            String subject = "Welcome to " + appName + "!";
            String htmlContent = templateEngine.process("email/welcome-email", createContext(variables));

            sendHtmlEmail(to, subject, htmlContent);
            log.info("Welcome email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", to, e);
            // Don't throw exception for welcome email failures
        }
    }

    /**
     * Send assignment notification email
     */
    @Async
    @Override
    public void sendAssignmentNotification(String to, String studentName, String assignmentTitle, String instructorName, LocalDateTime dueDate) {
        log.info("Sending assignment notification to: {}", to);

        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("studentName", studentName);
            variables.put("assignmentTitle", assignmentTitle);
            variables.put("instructorName", instructorName);
            variables.put("dueDate", dueDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            variables.put("assignmentsUrl", appUrl + "/student/assignments");
            variables.put("appName", appName);
            variables.put("year", LocalDateTime.now().getYear());

            String subject = "New Assignment: " + assignmentTitle;
            String htmlContent = templateEngine.process("email/assignment-notification", createContext(variables));

            sendHtmlEmail(to, subject, htmlContent);
            log.info("Assignment notification sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send assignment notification to: {}", to, e);
        }
    }

    /**
     * Send badge earned notification
     */
    @Async
    @Override
    public void sendBadgeEarnedEmail(String to, String studentName, String badgeName, String badgeDescription) {
        log.info("Sending badge earned notification to: {}", to);

        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("studentName", studentName);
            variables.put("badgeName", badgeName);
            variables.put("badgeDescription", badgeDescription);
            variables.put("badgesUrl", appUrl + "/student/badges");
            variables.put("appName", appName);
            variables.put("year", LocalDateTime.now().getYear());

            String subject = "Congratulations! You've Earned a Badge";
            String htmlContent = templateEngine.process("email/badge-earned", createContext(variables));

            sendHtmlEmail(to, subject, htmlContent);
            log.info("Badge earned email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send badge earned email to: {}", to, e);
        }
    }

    /**
     * Send HTML email
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    /**
     * Send plain text email
     */
    @Async
    @Override
    public void sendSimpleEmail(String to, String subject, String text) {
        log.info("Sending simple email to: {}", to);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("Simple email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send simple email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Create Thymeleaf context with variables
     */
    private Context createContext(Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return context;
    }
}