package com.vroom.learning.repository;

import com.vroom.learning.model.entity.Assignment;
import com.vroom.learning.model.enums.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for Assignment entity operations
 */
@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {

    /**
     * Find assignments for a student
     */
    List<Assignment> findByStudentIdOrderByDueDateAsc(UUID studentId);

    /**
     * Find assignments created by instructor
     */
    List<Assignment> findByInstructorIdOrderByCreatedAtDesc(UUID instructorId);

    /**
     * Find assignments by status for student
     */
    List<Assignment> findByStudentIdAndStatus(UUID studentId, AssignmentStatus status);

    /**
     * Find assignments by status for instructor
     */
    List<Assignment> findByInstructorIdAndStatus(UUID instructorId, AssignmentStatus status);

    /**
     * Find overdue assignments for student
     */
    @Query("SELECT a FROM Assignment a WHERE a.studentId = :studentId AND a.dueDate < :now AND a.status NOT IN ('COMPLETED', 'GRADED')")
    List<Assignment> findOverdueAssignments(@Param("studentId") UUID studentId, @Param("now") LocalDateTime now);

    /**
     * Find upcoming assignments (due within next N days)
     */
    @Query("SELECT a FROM Assignment a WHERE a.studentId = :studentId AND a.dueDate BETWEEN :now AND :future AND a.status = 'PENDING'")
    List<Assignment> findUpcomingAssignments(@Param("studentId") UUID studentId,
                                             @Param("now") LocalDateTime now,
                                             @Param("future") LocalDateTime future);

    /**
     * Count pending assignments for student
     */
    long countByStudentIdAndStatus(UUID studentId, AssignmentStatus status);

    /**
     * Find assignments that need grading
     */
    List<Assignment> findByInstructorIdAndStatusOrderByCompletedAtAsc(UUID instructorId, AssignmentStatus status);

    /**
     * Get completion rate for instructor's assignments
     */
    @Query("SELECT (COUNT(a) * 100.0 / (SELECT COUNT(a2) FROM Assignment a2 WHERE a2.instructorId = :instructorId)) " +
            "FROM Assignment a WHERE a.instructorId = :instructorId AND a.status = 'COMPLETED'")
    Double getCompletionRateByInstructor(@Param("instructorId") UUID instructorId);
}