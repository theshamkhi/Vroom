package com.vroom.content.model.enums;

/**
 * Difficulty levels for scenarios
 */
public enum Difficulty {
    /**
     * Beginner level - Basic driving situations
     */
    BEGINNER("Beginner", 1),

    /**
     * Intermediate level - Moderate complexity
     */
    INTERMEDIATE("Intermediate", 2),

    /**
     * Advanced level - Complex scenarios requiring experience
     */
    ADVANCED("Advanced", 3);

    private final String displayName;
    private final int level;

    Difficulty(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }
}