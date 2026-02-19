package com.vroom.content.repository;

import com.vroom.content.model.entity.Question;
import com.vroom.content.model.enums.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for Question entity operations
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {

    /**
     * Find all questions for a scenario, ordered by orderIndex
     */
    List<Question> findByScenarioIdOrderByOrderIndexAsc(UUID scenarioId);

    /**
     * Find questions by type
     */
    List<Question> findByType(QuestionType type);

    /**
     * Find questions by scenario and type
     */
    List<Question> findByScenarioIdAndType(UUID scenarioId, QuestionType type);

    /**
     * Count questions for a scenario
     */
    long countByScenarioId(UUID scenarioId);

    /**
     * Find questions with hints
     */
    @Query("SELECT q FROM Question q WHERE q.scenarioId = :scenarioId AND q.hint IS NOT NULL")
    List<Question> findQuestionsWithHints(@Param("scenarioId") UUID scenarioId);

    /**
     * Find timed questions for a scenario
     */
    @Query("SELECT q FROM Question q WHERE q.scenarioId = :scenarioId AND q.timeLimitSeconds IS NOT NULL")
    List<Question> findTimedQuestions(@Param("scenarioId") UUID scenarioId);

    /**
     * Find difficult questions (low success rate)
     */
    @Query("SELECT q FROM Question q WHERE q.scenarioId = :scenarioId AND q.attemptCount > :minAttempts " +
            "AND (CAST(q.correctCount AS double) / CAST(q.attemptCount AS double)) < :threshold")
    List<Question> findDifficultQuestions(@Param("scenarioId") UUID scenarioId,
                                          @Param("minAttempts") Integer minAttempts,
                                          @Param("threshold") Double threshold);

    /**
     * Get average success rate for scenario
     */
    @Query("SELECT AVG(CAST(q.correctCount AS double) / NULLIF(CAST(q.attemptCount AS double), 0)) * 100 " +
            "FROM Question q WHERE q.scenarioId = :scenarioId AND q.attemptCount > 0")
    Double getAverageSuccessRate(@Param("scenarioId") UUID scenarioId);

    /**
     * Find questions by points range
     */
    @Query("SELECT q FROM Question q WHERE q.scenarioId = :scenarioId AND q.points BETWEEN :minPoints AND :maxPoints")
    List<Question> findByPointsRange(@Param("scenarioId") UUID scenarioId,
                                     @Param("minPoints") Integer minPoints,
                                     @Param("maxPoints") Integer maxPoints);

    /**
     * Get total possible points for a scenario
     */
    @Query("SELECT SUM(q.points) FROM Question q WHERE q.scenarioId = :scenarioId")
    Integer getTotalPointsForScenario(@Param("scenarioId") UUID scenarioId);

    /**
     * Find most answered questions
     */
    @Query("SELECT q FROM Question q WHERE q.scenarioId = :scenarioId ORDER BY q.attemptCount DESC")
    List<Question> findMostAnsweredQuestions(@Param("scenarioId") UUID scenarioId);

    /**
     * Delete all questions for a scenario
     */
    void deleteByScenarioId(UUID scenarioId);
}