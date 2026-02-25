package com.vroom.learning.repository;

import com.vroom.learning.model.entity.StudentScenario;
import com.vroom.learning.model.enums.CompletionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for StudentScenario entity operations
 */
@Repository
public interface StudentScenarioRepository extends JpaRepository<StudentScenario, UUID> {

    /**
     * Find student's progress on a specific scenario
     */
    Optional<StudentScenario> findByStudentIdAndScenarioId(UUID studentId, UUID scenarioId);

    /**
     * Find all scenarios for a student
     */
    List<StudentScenario> findByStudentIdOrderByLastAccessedAtDesc(UUID studentId);

    /**
     * Find scenarios by status for a student
     */
    List<StudentScenario> findByStudentIdAndStatus(UUID studentId, CompletionStatus status);

    /**
     * Find completed scenarios for a student
     */
    @Query("SELECT ss FROM StudentScenario ss WHERE ss.studentId = :studentId AND ss.status IN ('COMPLETED_PASSED', 'COMPLETED_FAILED')")
    List<StudentScenario> findCompletedScenarios(@Param("studentId") UUID studentId);

    /**
     * Find passed scenarios for a student
     */
    @Query("SELECT ss FROM StudentScenario ss WHERE ss.studentId = :studentId AND ss.status IN :statuses")
    List<StudentScenario> findByStudentIdAndStatusIn(@Param("studentId") UUID studentId, @Param("statuses") List<CompletionStatus> statuses);

    /**
     * Count scenarios by status for a student
     */
    long countByStudentIdAndStatus(UUID studentId, CompletionStatus status);

    /**
     * Get student's average score across all scenarios
     */
    @Query("SELECT AVG(ss.highestScore) FROM StudentScenario ss WHERE ss.studentId = :studentId AND ss.highestScore IS NOT NULL")
    Double getAverageScoreByStudent(@Param("studentId") UUID studentId);

    /**
     * Get total time spent by student
     */
    @Query("SELECT SUM(ss.timeSpentSeconds) FROM StudentScenario ss WHERE ss.studentId = :studentId")
    Integer getTotalTimeSpentByStudent(@Param("studentId") UUID studentId);

    /**
     * Get total points earned by student
     */
    @Query("SELECT SUM(ss.totalPointsEarned) FROM StudentScenario ss WHERE ss.studentId = :studentId")
    Integer getTotalPointsByStudent(@Param("studentId") UUID studentId);

    /**
     * Find students who completed a specific scenario
     */
    List<StudentScenario> findByScenarioIdAndStatus(UUID scenarioId, CompletionStatus status);

    /**
     * Check if student has started a scenario
     */
    boolean existsByStudentIdAndScenarioId(UUID studentId, UUID scenarioId);

    /**
     * Find top performers for a scenario
     */
    @Query("SELECT ss FROM StudentScenario ss WHERE ss.scenarioId = :scenarioId ORDER BY ss.highestScore DESC")
    List<StudentScenario> findTopPerformers(@Param("scenarioId") UUID scenarioId);
}