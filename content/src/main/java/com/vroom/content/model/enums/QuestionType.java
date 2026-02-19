package com.vroom.content.model.enums;

/**
 * Types of questions that can be asked at interaction points
 */
public enum QuestionType {
    /**
     * Multiple choice with one or more correct answers
     */
    MULTIPLE_CHOICE("Multiple Choice", true),

    /**
     * Single choice - only one correct answer
     */
    SINGLE_CHOICE("Single Choice", false),

    /**
     * True or False question
     */
    TRUE_FALSE("True/False", false),

    /**
     * Identify hazard in the video
     */
    HAZARD_IDENTIFICATION("Hazard Identification", false),

    /**
     * Choose the correct action to take
     */
    ACTION_CHOICE("Action Choice", false);

    private final String displayName;
    private final boolean allowsMultipleAnswers;

    QuestionType(String displayName, boolean allowsMultipleAnswers) {
        this.displayName = displayName;
        this.allowsMultipleAnswers = allowsMultipleAnswers;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean allowsMultipleAnswers() {
        return allowsMultipleAnswers;
    }
}