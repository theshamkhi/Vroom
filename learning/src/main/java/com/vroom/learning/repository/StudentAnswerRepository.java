package com.vroom.learning.repository;

import com.vroom.learning.model.entity.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for StudentAnswer entity operations
 */
@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, UUID> {

    /**
     * Find student's answers for a scenario
     */
    List<StudentAnswer> findByStudentIdAndScenarioId(UUID studentId, UUID scenarioId);

    /**
     * Find student's answer to a specific question
     */
    Optional<StudentAnswer> findByStudentIdAndQuestionId(UUID studentId, UUID questionId);

    /**
     * Find all answers for a question (across all students)
     */
    List<StudentAnswer> findByQuestionId(UUID questionId);

    /**
     * Count correct answers for a student in a scenario
     */
    long countByStudentIdAndScenarioIdAndIsCorrectTrue(UUID studentId, UUID scenarioId);

    /**
     * Count total answers for a student in a scenario
     */
    long countByStudentIdAndScenarioId(UUID studentId, UUID scenarioId);

    /**
     * Find student's answers for a specific scenario attempt
     */
    List<StudentAnswer> findByStudentScenarioId(UUID studentScenarioId);

    /**
     * Get success rate for a question (across all students)
     */
    @Query("SELECT (COUNT(sa) * 100.0 / (SELECT COUNT(sa2) FROM StudentAnswer sa2 WHERE sa2.questionId = :questionId)) " +
            "FROM StudentAnswer sa WHERE sa.questionId = :questionId AND sa.isCorrect = true")
    Double getQuestionSuccessRate(@Param("questionId") UUID questionId);

    /**
     * Find answers where hint was used
     */
    List<StudentAnswer> findByStudentIdAndScenarioIdAndHintUsedTrue(UUID studentId, UUID scenarioId);
}