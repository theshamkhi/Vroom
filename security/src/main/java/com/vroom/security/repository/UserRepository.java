package com.vroom.security.repository;

import com.vroom.security.model.entity.User;
import com.vroom.security.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for User entity operations
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by email (case-insensitive)
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Check if user exists by email
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Find user by email verification token
     */
    Optional<User> findByEmailVerificationToken(String token);

    /**
     * Find user by password reset token
     */
    Optional<User> findByPasswordResetToken(String token);

    /**
     * Find all users by role
     */
    List<User> findByRole(Role role);

    /**
     * Find all enabled users
     */
    List<User> findByEnabledTrue();

    /**
     * Find users created after a specific date
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find users by role and enabled status
     */
    List<User> findByRoleAndEnabled(Role role, Boolean enabled);

    /**
     * Update last login timestamp
     */
    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :loginTime WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") UUID userId, @Param("loginTime") LocalDateTime loginTime);

    /**
     * Count users by role
     */
    long countByRole(Role role);

    /**
     * Find users who haven't logged in since a specific date
     */
    @Query("SELECT u FROM User u WHERE u.lastLoginAt < :date OR u.lastLoginAt IS NULL")
    List<User> findInactiveUsersSince(@Param("date") LocalDateTime date);

    /**
     * Delete expired email verification tokens
     */
    @Modifying
    @Query("UPDATE User u SET u.emailVerificationToken = null, u.emailVerificationTokenExpiry = null " +
            "WHERE u.emailVerificationTokenExpiry < :now")
    int clearExpiredEmailVerificationTokens(@Param("now") LocalDateTime now);

    /**
     * Delete expired password reset tokens
     */
    @Modifying
    @Query("UPDATE User u SET u.passwordResetToken = null, u.passwordResetTokenExpiry = null " +
            "WHERE u.passwordResetTokenExpiry < :now")
    int clearExpiredPasswordResetTokens(@Param("now") LocalDateTime now);
}