package com.vroom.content.model.enums;

/**
 * Types of badges that can be earned
 */
public enum BadgeType {
    /**
     * Earned by completing scenarios
     */
    COMPLETION("Completion Badge", "Awarded for completing scenarios"),

    /**
     * Earned by achieving high scores
     */
    PERFORMANCE("Performance Badge", "Awarded for high performance"),

    /**
     * Earned by mastering specific skills
     */
    SKILL_MASTERY("Skill Mastery Badge", "Awarded for mastering specific skills"),

    /**
     * Earned by completing scenarios in a theme
     */
    THEME_MASTER("Theme Master Badge", "Awarded for mastering all scenarios in a theme"),

    /**
     * Earned by maintaining a streak
     */
    STREAK("Streak Badge", "Awarded for maintaining learning streaks"),

    /**
     * Earned by helping others or special achievements
     */
    SPECIAL("Special Badge", "Special achievements and milestones");

    private final String displayName;
    private final String description;

    BadgeType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}