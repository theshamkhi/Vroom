package com.vroom.learning.model.enums;

/**
 * Completion status for scenarios
 */
public enum CompletionStatus {
    /**
     * Not started yet
     */
    NOT_STARTED("Not Started"),

    /**
     * In progress
     */
    IN_PROGRESS("In Progress"),

    /**
     * Completed but didn't pass
     */
    COMPLETED_FAILED("Completed - Failed"),

    /**
     * Completed and passed
     */
    COMPLETED_PASSED("Completed - Passed");

    private final String displayName;

    CompletionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isPassed() {
        return this == COMPLETED_PASSED;
    }

    public boolean isCompleted() {
        return this == COMPLETED_PASSED || this == COMPLETED_FAILED;
    }
}