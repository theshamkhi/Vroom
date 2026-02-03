package com.vroom.security.model.enums;

/**
 * User roles in the Vroom platform
 */
public enum Role {
    /**
     * Student role - Can access learning materials and track progress
     */
    STUDENT,

    /**
     * Instructor role - Can manage students, assign lessons, and view analytics
     */
    INSTRUCTOR,

    /**
     * Administrator role - Full system access including content management
     */
    ADMIN
}