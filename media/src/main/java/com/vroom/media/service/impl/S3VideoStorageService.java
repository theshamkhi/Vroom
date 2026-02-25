package com.vroom.media.service.impl;

import com.vroom.media.service.storage.VideoStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

/**
 * AWS S3 implementation of VideoStorageService
 * Stores videos in S3 bucket
 */
@Service
@Profile("cloud")
@RequiredArgsConstructor
@Slf4j
public class S3VideoStorageService implements VideoStorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.video-prefix:videos/}")
    private String videoPrefix;

    @Value("${aws.s3.thumbnail-prefix:thumbnails/}")
    private String thumbnailPrefix;

    @Value("${aws.s3.presigned-url-duration:3600}")
    private long presignedUrlDuration; // in seconds

    @Override
    public String uploadVideo(MultipartFile file, UUID videoId) throws IOException {
        log.info("Uploading video to S3: {}", videoId);

        // Get file extension
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".mp4";

        // Create S3 key
        String key = videoPrefix + videoId.toString() + extension;

        // Upload to S3
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            log.info("Video uploaded successfully to S3: {}", key);
            return key;
        } catch (Exception e) {
            log.error("Failed to upload video to S3", e);
            throw new IOException("Failed to upload video to S3", e);
        }
    }

    @Override
    public InputStream getVideoStream(String filePath) throws IOException {
        log.debug("Getting video stream from S3: {}", filePath);

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .build();

            return s3Client.getObject(getObjectRequest);
        } catch (Exception e) {
            log.error("Failed to get video stream from S3", e);
            throw new IOException("Failed to get video stream from S3", e);
        }
    }

    @Override
    public String getVideoUrl(String filePath) {
        log.debug("Generating presigned URL for: {}", filePath);

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(presignedUrlDuration))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

            return presignedRequest.url().toString();
        } catch (Exception e) {
            log.error("Failed to generate presigned URL", e);
            return null;
        }
    }

    @Override
    public void deleteVideo(String filePath) throws IOException {
        log.info("Deleting video from S3: {}", filePath);

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("Video deleted successfully from S3: {}", filePath);
        } catch (Exception e) {
            log.error("Failed to delete video from S3", e);
            throw new IOException("Failed to delete video from S3", e);
        }
    }

    @Override
    public String uploadThumbnail(byte[] thumbnailData, UUID videoId) throws IOException {
        log.info("Uploading thumbnail to S3: {}", videoId);

        // Create S3 key
        String key = thumbnailPrefix + videoId.toString() + ".jpg";

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType("image/jpeg")
                    .contentLength((long) thumbnailData.length)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(thumbnailData));

            log.info("Thumbnail uploaded successfully to S3: {}", key);
            return key;
        } catch (Exception e) {
            log.error("Failed to upload thumbnail to S3", e);
            throw new IOException("Failed to upload thumbnail to S3", e);
        }
    }

    @Override
    public String getThumbnailUrl(String thumbnailPath) {
        return getVideoUrl(thumbnailPath); // Same presigned URL logic
    }

    @Override
    public boolean fileExists(String filePath) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            log.error("Error checking if file exists in S3", e);
            return false;
        }
    }

    @Override
    public String getStorageType() {
        return "S3";
    }
}