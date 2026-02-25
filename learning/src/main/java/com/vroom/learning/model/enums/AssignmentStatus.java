package com.vroom.learning.model.enums;

/**
 * Status of student assignments
 */
public enum AssignmentStatus {
    /**
     * Assignment has been created but not started
     */
    PENDING("Pending"),

    /**
     * Student is currently working on it
     */
    IN_PROGRESS("In Progress"),

    /**
     * Student has submitted/completed
     */
    COMPLETED("Completed"),

    /**
     * Assignment is overdue
     */
    OVERDUE("Overdue"),

    /**
     * Instructor has reviewed and graded
     */
    GRADED("Graded");

    private final String displayName;

    AssignmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}