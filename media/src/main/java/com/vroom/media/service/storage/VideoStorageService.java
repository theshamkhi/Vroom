package com.vroom.media.service.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Interface for video storage operations
 * Allows switching between local and cloud storage
 */
public interface VideoStorageService {

    /**
     * Upload video file and return the file path/key
     */
    String uploadVideo(MultipartFile file, UUID videoId) throws IOException;

    /**
     * Get video file as InputStream for streaming
     */
    InputStream getVideoStream(String filePath) throws IOException;

    /**
     * Get video URL (presigned for S3, local path for local storage)
     */
    String getVideoUrl(String filePath);

    /**
     * Delete video file
     */
    void deleteVideo(String filePath) throws IOException;

    /**
     * Upload thumbnail and return the file path/key
     */
    String uploadThumbnail(byte[] thumbnailData, UUID videoId) throws IOException;

    /**
     * Get thumbnail URL
     */
    String getThumbnailUrl(String thumbnailPath);

    /**
     * Check if file exists
     */
    boolean fileExists(String filePath);

    /**
     * Get storage type identifier (LOCAL or S3)
     */
    String getStorageType();
}