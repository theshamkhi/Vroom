package com.vroom.content.repository;

import com.vroom.content.model.entity.StudentBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for StudentBadge entity operations
 */
@Repository
public interface StudentBadgeRepository extends JpaRepository<StudentBadge, UUID> {

    /**
     * Find all badges for a student
     */
    List<StudentBadge> findByStudentIdOrderByEarnedAtDesc(UUID studentId);

    /**
     * Find displayed badges for a student
     */
    List<StudentBadge> findByStudentIdAndDisplayedTrueOrderByEarnedAtDesc(UUID studentId);

    /**
     * Find specific student-badge combination
     */
    Optional<StudentBadge> findByStudentIdAndBadgeId(UUID studentId, UUID badgeId);

    /**
     * Check if student has earned a badge
     */
    boolean existsByStudentIdAndBadgeId(UUID studentId, UUID badgeId);

    /**
     * Find students who earned a specific badge
     */
    List<StudentBadge> findByBadgeIdOrderByEarnedAtDesc(UUID badgeId);

    /**
     * Find recently earned badges for a student
     */
    @Query("SELECT sb FROM StudentBadge sb WHERE sb.studentId = :studentId AND sb.earnedAt >= :since ORDER BY sb.earnedAt DESC")
    List<StudentBadge> findRecentlyEarned(@Param("studentId") UUID studentId, @Param("since") LocalDateTime since);

    /**
     * Find unnotified badges for a student
     */
    List<StudentBadge> findByStudentIdAndNotifiedFalse(UUID studentId);

    /**
     * Count badges earned by student
     */
    long countByStudentId(UUID studentId);

    /**
     * Count students who earned a badge
     */
    long countByBadgeId(UUID badgeId);

    /**
     * Find badges earned in a specific scenario
     */
    List<StudentBadge> findByStudentIdAndEarnedInScenarioId(UUID studentId, UUID scenarioId);

    /**
     * Get total badges earned across all students
     */
    @Query("SELECT COUNT(sb) FROM StudentBadge sb")
    long getTotalBadgesEarnedAcrossAll();

    /**
     * Find top badge earners
     */
    @Query("SELECT sb.studentId, COUNT(sb) as badgeCount FROM StudentBadge sb " +
            "GROUP BY sb.studentId ORDER BY badgeCount DESC")
    List<Object[]> findTopBadgeEarners();

    /**
     * Find students with most recent badges
     */
    @Query("SELECT sb FROM StudentBadge sb WHERE sb.earnedAt >= :since GROUP BY sb.studentId " +
            "HAVING COUNT(sb) >= :minCount")
    List<StudentBadge> findActiveStudents(@Param("since") LocalDateTime since, @Param("minCount") Long minCount);

    /**
     * Delete all badges for a student
     */
    void deleteByStudentId(UUID studentId);
}