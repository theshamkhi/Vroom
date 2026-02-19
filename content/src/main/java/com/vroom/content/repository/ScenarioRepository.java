package com.vroom.content.repository;

import com.vroom.content.model.entity.Scenario;
import com.vroom.content.model.enums.Difficulty;
import com.vroom.content.model.enums.Theme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Scenario entity operations
 */
@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, UUID> {

    /**
     * Find scenario by ID only if published
     */
    Optional<Scenario> findByIdAndPublishedTrue(UUID id);

    /**
     * Find all published scenarios
     */
    List<Scenario> findByPublishedTrueOrderByCreatedAtDesc();

    /**
     * Find all published scenarios (paginated)
     */
    Page<Scenario> findByPublishedTrue(Pageable pageable);

    /**
     * Find scenarios by difficulty
     */
    List<Scenario> findByDifficultyAndPublishedTrue(Difficulty difficulty);

    /**
     * Find scenarios by theme
     */
    List<Scenario> findByThemeAndPublishedTrue(Theme theme);

    /**
     * Find scenarios by difficulty and theme
     */
    List<Scenario> findByDifficultyAndThemeAndPublishedTrue(Difficulty difficulty, Theme theme);

    /**
     * Find scenarios by creator
     */
    List<Scenario> findByCreatedBy(UUID createdBy);

    /**
     * Search scenarios by title (case-insensitive)
     */
    @Query("SELECT s FROM Scenario s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%')) AND s.published = true")
    List<Scenario> searchByTitle(@Param("title") String title);

    /**
     * Search scenarios by title or description
     */
    @Query("SELECT s FROM Scenario s WHERE (LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND s.published = true")
    List<Scenario> searchByKeyword(@Param("keyword") String keyword);

    /**
     * Find scenarios by tag
     */
    @Query("SELECT s FROM Scenario s JOIN s.tags t WHERE t = :tag AND s.published = true")
    List<Scenario> findByTag(@Param("tag") String tag);

    /**
     * Find scenarios with multiple tags (contains all tags)
     */
    @Query("SELECT s FROM Scenario s JOIN s.tags t WHERE t IN :tags AND s.published = true GROUP BY s HAVING COUNT(DISTINCT t) = :tagCount")
    List<Scenario> findByAllTags(@Param("tags") List<String> tags, @Param("tagCount") Long tagCount);

    /**
     * Find top rated scenarios (by average score)
     */
    @Query("SELECT s FROM Scenario s WHERE s.published = true AND s.averageScore IS NOT NULL ORDER BY s.averageScore DESC")
    List<Scenario> findTopRatedScenarios(Pageable pageable);

    /**
     * Find most popular scenarios (by completion count)
     */
    @Query("SELECT s FROM Scenario s WHERE s.published = true ORDER BY s.completionCount DESC")
    List<Scenario> findMostPopularScenarios(Pageable pageable);

    /**
     * Find scenarios with passing score threshold
     */
    @Query("SELECT s FROM Scenario s WHERE s.published = true AND s.passingScore <= :maxPassingScore")
    List<Scenario> findEasyScenarios(@Param("maxPassingScore") Integer maxPassingScore);

    /**
     * Find scenarios by duration range
     */
    @Query("SELECT s FROM Scenario s WHERE s.published = true AND s.durationSeconds BETWEEN :minDuration AND :maxDuration")
    List<Scenario> findByDurationRange(@Param("minDuration") Integer minDuration, @Param("maxDuration") Integer maxDuration);

    /**
     * Find scenarios without prerequisites (good for beginners)
     */
    @Query("SELECT s FROM Scenario s WHERE s.published = true AND s.prerequisiteIds IS EMPTY")
    List<Scenario> findScenariosWithoutPrerequisites();

    /**
     * Count scenarios by theme
     */
    long countByThemeAndPublishedTrue(Theme theme);

    /**
     * Count scenarios by difficulty
     */
    long countByDifficultyAndPublishedTrue(Difficulty difficulty);

    /**
     * Get average scenario completion time
     */
    @Query("SELECT AVG(s.averageCompletionTime) FROM Scenario s WHERE s.published = true AND s.averageCompletionTime IS NOT NULL")
    Double getAverageCompletionTimeAcrossAll();

    /**
     * Get average score across all scenarios
     */
    @Query("SELECT AVG(s.averageScore) FROM Scenario s WHERE s.published = true AND s.averageScore IS NOT NULL")
    Double getAverageScoreAcrossAll();

    /**
     * Find scenarios that need improvement (low average score)
     */
    @Query("SELECT s FROM Scenario s WHERE s.published = true AND s.averageScore < :threshold AND s.completionCount > :minCompletions")
    List<Scenario> findScenariosNeedingImprovement(@Param("threshold") Double threshold, @Param("minCompletions") Integer minCompletions);

    /**
     * Find recently published scenarios
     */
    @Query("SELECT s FROM Scenario s WHERE s.published = true ORDER BY s.publishedAt DESC")
    List<Scenario> findRecentlyPublished(Pageable pageable);

    /**
     * Check if scenario has video
     */
    boolean existsByIdAndVideoIdIsNotNull(UUID id);
}