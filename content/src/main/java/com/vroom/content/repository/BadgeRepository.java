package com.vroom.content.repository;

import com.vroom.content.model.entity.Badge;
import com.vroom.content.model.enums.BadgeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Badge entity operations
 */
@Repository
public interface BadgeRepository extends JpaRepository<Badge, UUID> {

    /**
     * Find all active badges
     */
    List<Badge> findByActiveTrueOrderByNameAsc();

    /**
     * Find badges by type
     */
    List<Badge> findByTypeAndActiveTrue(BadgeType type);

    /**
     * Find badge by name
     */
    Optional<Badge> findByName(String name);

    /**
     * Find badges related to a specific scenario
     */
    List<Badge> findByRelatedScenarioIdAndActiveTrue(UUID scenarioId);

    /**
     * Find badges related to a theme
     */
    List<Badge> findByRelatedThemeAndActiveTrue(String theme);

    /**
     * Find most earned badges
     */
    @Query("SELECT b FROM Badge b WHERE b.active = true ORDER BY b.earnedCount DESC")
    List<Badge> findMostEarnedBadges();

    /**
     * Find rare badges (least earned)
     */
    @Query("SELECT b FROM Badge b WHERE b.active = true AND b.earnedCount > 0 ORDER BY b.earnedCount ASC")
    List<Badge> findRareBadges();

    /**
     * Find badges by minimum point value
     */
    @Query("SELECT b FROM Badge b WHERE b.active = true AND b.pointsValue >= :minPoints ORDER BY b.pointsValue DESC")
    List<Badge> findHighValueBadges(@Param("minPoints") Integer minPoints);

    /**
     * Count active badges by type
     */
    long countByTypeAndActiveTrue(BadgeType type);

    /**
     * Get total badges earned count
     */
    @Query("SELECT SUM(b.earnedCount) FROM Badge b WHERE b.active = true")
    Long getTotalBadgesEarned();

    /**
     * Find newly created badges (for discovery)
     */
    @Query("SELECT b FROM Badge b WHERE b.active = true ORDER BY b.createdAt DESC")
    List<Badge> findNewestBadges();

    /**
     * Check if badge name exists
     */
    boolean existsByName(String name);
}