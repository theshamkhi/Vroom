package com.vroom.media.repository;

import com.vroom.media.model.entity.Video;
import com.vroom.media.model.enums.VideoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Video entity operations
 */
@Repository
public interface VideoRepository extends JpaRepository<Video, UUID> {

    /**
     * Find videos by status
     */
    List<Video> findByStatus(VideoStatus status);

    /**
     * Find videos uploaded by user
     */
    List<Video> findByUploadedByOrderByCreatedAtDesc(UUID uploadedBy);

    /**
     * Find public videos
     */
    List<Video> findByIsPublicTrueOrderByCreatedAtDesc();

    /**
     * Find ready videos
     */
    List<Video> findByStatusOrderByCreatedAtDesc(VideoStatus status);

    /**
     * Find video by stored filename
     */
    Optional<Video> findByStoredFilename(String storedFilename);

    /**
     * Search videos by title
     */
    @Query("SELECT v FROM Video v WHERE LOWER(v.title) LIKE LOWER(CONCAT('%', :keyword, '%')) AND v.status = 'READY'")
    List<Video> searchByTitle(@Param("keyword") String keyword);

    /**
     * Find videos by storage type
     */
    List<Video> findByStorageType(String storageType);

    /**
     * Find videos uploaded in date range
     */
    @Query("SELECT v FROM Video v WHERE v.createdAt BETWEEN :startDate AND :endDate ORDER BY v.createdAt DESC")
    List<Video> findByUploadDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Get total storage used by user (in bytes)
     */
    @Query("SELECT SUM(v.fileSizeBytes) FROM Video v WHERE v.uploadedBy = :userId AND v.status != 'DELETED'")
    Long getTotalStorageByUser(@Param("userId") UUID userId);

    /**
     * Get total storage used (in bytes)
     */
    @Query("SELECT SUM(v.fileSizeBytes) FROM Video v WHERE v.status != 'DELETED'")
    Long getTotalStorageUsed();

    /**
     * Count videos by status
     */
    long countByStatus(VideoStatus status);

    /**
     * Count videos by uploader
     */
    long countByUploadedBy(UUID uploadedBy);

    /**
     * Find most viewed videos
     */
    @Query("SELECT v FROM Video v WHERE v.status = 'READY' ORDER BY v.viewCount DESC")
    List<Video> findMostViewedVideos();

    /**
     * Find recently uploaded videos
     */
    @Query("SELECT v FROM Video v WHERE v.status = 'READY' ORDER BY v.createdAt DESC")
    List<Video> findRecentlyUploaded();

    /**
     * Delete old failed videos
     */
    @Query("DELETE FROM Video v WHERE v.status = 'FAILED' AND v.createdAt < :date")
    void deleteOldFailedVideos(@Param("date") LocalDateTime date);
}