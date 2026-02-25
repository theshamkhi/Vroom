package com.vroom.media.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Service for generating video thumbnails
 */
@Service
@Slf4j
public class ThumbnailService {

    /**
     * Generate thumbnail from video file
     * For now, creates a simple placeholder image
     * TODO: FFmpeg or similar for actual video frame extraction
     */
    public byte[] generateThumbnail(MultipartFile videoFile) throws IOException {
        log.info("Generating thumbnail for video: {}", videoFile.getOriginalFilename());

        // Create a placeholder thumbnail (640x360)
        int width = 640;
        int height = 360;

        BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = thumbnail.createGraphics();

        // Create gradient background
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(103, 126, 234),
                width, height, new Color(118, 75, 162)
        );
        graphics.setPaint(gradient);
        graphics.fillRect(0, 0, width, height);

        // Add play icon
        graphics.setColor(Color.WHITE);
        int[] xPoints = {width/2 - 30, width/2 - 30, width/2 + 30};
        int[] yPoints = {height/2 - 40, height/2 + 40, height/2};
        graphics.fillPolygon(xPoints, yPoints, 3);

        // Add filename text
        graphics.setFont(new Font("Arial", Font.BOLD, 16));
        String filename = videoFile.getOriginalFilename();
        if (filename != null && filename.length() > 40) {
            filename = filename.substring(0, 37) + "...";
        }
        graphics.drawString(filename != null ? filename : "Video", 20, height - 20);

        graphics.dispose();

        // Convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(thumbnail, "jpg", baos);

        byte[] thumbnailData = baos.toByteArray();
        log.info("Thumbnail generated successfully (size: {} bytes)", thumbnailData.length);

        return thumbnailData;
    }
}