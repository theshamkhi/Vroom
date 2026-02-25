package com.vroom.media.model.enums;

/**
 * Video processing status
 */
public enum VideoStatus {
    /**
     * Video is being uploaded
     */
    UPLOADING("Uploading"),

    /**
     * Video upload completed, processing thumbnail
     */
    PROCESSING("Processing"),

    /**
     * Video is ready to be used
     */
    READY("Ready"),

    /**
     * Video processing failed
     */
    FAILED("Failed"),

    /**
     * Video has been deleted
     */
    DELETED("Deleted");

    private final String displayName;

    VideoStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}