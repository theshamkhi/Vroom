package com.vroom.content.repository;

import com.vroom.content.model.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for Answer entity operations
 */
@Repository
public interface AnswerRepository extends JpaRepository<Answer, UUID> {

    /**
     * Find all answers for a question, ordered by orderIndex
     */
    @Query("SELECT a FROM Answer a WHERE a.question.id = :questionId ORDER BY a.orderIndex ASC")
    List<Answer> findByQuestionIdOrderByOrderIndexAsc(@Param("questionId") UUID questionId);

    /**
     * Find correct answers for a question
     */
    @Query("SELECT a FROM Answer a WHERE a.question.id = :questionId AND a.isCorrect = true")
    List<Answer> findCorrectAnswers(@Param("questionId") UUID questionId);

    /**
     * Count answers for a question
     */
    @Query("SELECT COUNT(a) FROM Answer a WHERE a.question.id = :questionId")
    long countByQuestionId(@Param("questionId") UUID questionId);

    /**
     * Count correct answers for a question
     */
    @Query("SELECT COUNT(a) FROM Answer a WHERE a.question.id = :questionId AND a.isCorrect = true")
    long countCorrectAnswersByQuestionId(@Param("questionId") UUID questionId);

    /**
     * Find answers with images
     */
    @Query("SELECT a FROM Answer a WHERE a.question.id = :questionId AND a.imageUrl IS NOT NULL")
    List<Answer> findAnswersWithImages(@Param("questionId") UUID questionId);

    /**
     * Find answers with explanations
     */
    @Query("SELECT a FROM Answer a WHERE a.question.id = :questionId AND a.explanation IS NOT NULL")
    List<Answer> findAnswersWithExplanations(@Param("questionId") UUID questionId);

    /**
     * Delete all answers for a question
     */
    @Query("DELETE FROM Answer a WHERE a.question.id = :questionId")
    void deleteByQuestionId(@Param("questionId") UUID questionId);
}