package com.vroom.content.repository;

import com.vroom.content.model.entity.InteractionPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for InteractionPoint entity operations
 */
@Repository
public interface InteractionPointRepository extends JpaRepository<InteractionPoint, UUID> {

    /**
     * Find all interaction points for a scenario, ordered by timestamp
     */
    List<InteractionPoint> findByScenarioIdOrderByTimestampSecondsAsc(UUID scenarioId);

    /**
     * Find interaction points ordered by orderIndex
     */
    List<InteractionPoint> findByScenarioIdOrderByOrderIndexAsc(UUID scenarioId);

    /**
     * Find mandatory interaction points for a scenario
     */
    List<InteractionPoint> findByScenarioIdAndMandatoryTrueOrderByTimestampSecondsAsc(UUID scenarioId);

    /**
     * Find optional interaction points for a scenario
     */
    List<InteractionPoint> findByScenarioIdAndMandatoryFalseOrderByTimestampSecondsAsc(UUID scenarioId);

    /**
     * Find interaction point at specific timestamp
     */
    Optional<InteractionPoint> findByScenarioIdAndTimestampSeconds(UUID scenarioId, Integer timestampSeconds);

    /**
     * Find interaction points in time range
     */
    @Query("SELECT ip FROM InteractionPoint ip WHERE ip.scenarioId = :scenarioId " +
            "AND ip.timestampSeconds BETWEEN :startTime AND :endTime ORDER BY ip.timestampSeconds ASC")
    List<InteractionPoint> findInTimeRange(@Param("scenarioId") UUID scenarioId,
                                           @Param("startTime") Integer startTime,
                                           @Param("endTime") Integer endTime);

    /**
     * Count interaction points for a scenario
     */
    long countByScenarioId(UUID scenarioId);

    /**
     * Count mandatory interaction points
     */
    long countByScenarioIdAndMandatoryTrue(UUID scenarioId);

    /**
     * Get first interaction point (earliest timestamp)
     */
    @Query("SELECT ip FROM InteractionPoint ip WHERE ip.scenarioId = :scenarioId ORDER BY ip.timestampSeconds ASC")
    Optional<InteractionPoint> findFirstInteractionPoint(@Param("scenarioId") UUID scenarioId);

    /**
     * Get last interaction point (latest timestamp)
     */
    @Query("SELECT ip FROM InteractionPoint ip WHERE ip.scenarioId = :scenarioId ORDER BY ip.timestampSeconds DESC")
    Optional<InteractionPoint> findLastInteractionPoint(@Param("scenarioId") UUID scenarioId);

    /**
     * Check if interaction point exists at timestamp
     */
    boolean existsByScenarioIdAndTimestampSeconds(UUID scenarioId, Integer timestampSeconds);

    /**
     * Delete all interaction points for a scenario
     */
    void deleteByScenarioId(UUID scenarioId);

    /**
     * Find interaction points by question ID
     */
    List<InteractionPoint> findByQuestionId(UUID questionId);
}