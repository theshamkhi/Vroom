package com.vroom.media.service.impl;

import com.vroom.media.service.storage.VideoStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Local file system implementation of VideoStorageService
 * Stores videos in local directory
 */
@Service
@Profile({"local", "default"})
@Slf4j
public class LocalVideoStorageService implements VideoStorageService {

    @Value("${media.upload.directory:uploads}")
    private String uploadDirectory;

    @Value("${media.video.subdirectory:videos}")
    private String videoSubdirectory;

    @Value("${media.thumbnail.subdirectory:thumbnails}")
    private String thumbnailSubdirectory;

    @Value("${server.url:http://localhost:8080}")
    private String serverUrl;

    @Override
    public String uploadVideo(MultipartFile file, UUID videoId) throws IOException {
        log.info("Uploading video to local storage: {}", videoId);

        // Create directories if they don't exist
        Path videoDirectory = Paths.get(uploadDirectory, videoSubdirectory);
        Files.createDirectories(videoDirectory);

        // Get file extension
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".mp4";

        // Create unique filename
        String filename = videoId.toString() + extension;
        Path filePath = videoDirectory.resolve(filename);

        // Save file
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        log.info("Video uploaded successfully to: {}", filePath);
        return videoSubdirectory + "/" + filename;
    }

    @Override
    public InputStream getVideoStream(String filePath) throws IOException {
        log.debug("Getting video stream for: {}", filePath);

        Path fullPath = Paths.get(uploadDirectory, filePath);
        if (!Files.exists(fullPath)) {
            throw new IOException("Video file not found: " + filePath);
        }

        return new FileInputStream(fullPath.toFile());
    }

    @Override
    public String getVideoUrl(String filePath) {
        // Return URL that will be handled by controller
        return serverUrl + "/api/videos/stream/" + extractFilename(filePath);
    }

    @Override
    public void deleteVideo(String filePath) throws IOException {
        log.info("Deleting video from local storage: {}", filePath);

        Path fullPath = Paths.get(uploadDirectory, filePath);
        if (Files.exists(fullPath)) {
            Files.delete(fullPath);
            log.info("Video deleted successfully: {}", filePath);
        } else {
            log.warn("Video file not found for deletion: {}", filePath);
        }
    }

    @Override
    public String uploadThumbnail(byte[] thumbnailData, UUID videoId) throws IOException {
        log.info("Uploading thumbnail to local storage: {}", videoId);

        // Create directories if they don't exist
        Path thumbnailDirectory = Paths.get(uploadDirectory, thumbnailSubdirectory);
        Files.createDirectories(thumbnailDirectory);

        // Create filename
        String filename = videoId.toString() + ".jpg";
        Path filePath = thumbnailDirectory.resolve(filename);

        // Save thumbnail
        Files.write(filePath, thumbnailData);

        log.info("Thumbnail uploaded successfully to: {}", filePath);
        return thumbnailSubdirectory + "/" + filename;
    }

    @Override
    public String getThumbnailUrl(String thumbnailPath) {
        // Return URL that will be handled by controller
        return serverUrl + "/api/videos/thumbnail/" + extractFilename(thumbnailPath);
    }

    @Override
    public boolean fileExists(String filePath) {
        Path fullPath = Paths.get(uploadDirectory, filePath);
        return Files.exists(fullPath);
    }

    @Override
    public String getStorageType() {
        return "LOCAL";
    }

    /**
     * Extract filename from path
     */
    private String extractFilename(String path) {
        if (path == null) {
            return "";
        }
        int lastSlash = path.lastIndexOf('/');
        return lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
    }

    /**
     * Get file from path
     */
    public File getFile(String filePath) {
        Path fullPath = Paths.get(uploadDirectory, filePath);
        return fullPath.toFile();
    }
}