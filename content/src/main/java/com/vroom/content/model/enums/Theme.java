package com.vroom.content.model.enums;

/**
 * Themes/categories for driving scenarios
 */
public enum Theme {
    URBAN_DRIVING("Urban Driving", "City driving with traffic and pedestrians"),
    HIGHWAY("Highway", "High-speed highway driving"),
    PARKING("Parking", "Parking maneuvers and techniques"),
    INTERSECTIONS("Intersections", "Complex intersection navigation"),
    ROUNDABOUTS("Roundabouts", "Roundabout entry and exit"),
    WEATHER_CONDITIONS("Weather Conditions", "Driving in rain, fog, or snow"),
    NIGHT_DRIVING("Night Driving", "Driving in low visibility conditions"),
    EMERGENCY_SITUATIONS("Emergency Situations", "Handling emergencies and hazards"),
    DEFENSIVE_DRIVING("Defensive Driving", "Anticipating and avoiding hazards"),
    ROAD_SIGNS("Road Signs", "Understanding and following traffic signs"),
    PEDESTRIAN_SAFETY("Pedestrian Safety", "Sharing the road with pedestrians"),
    SCHOOL_ZONES("School Zones", "Safe driving near schools"),
    MERGING("Merging", "Merging onto highways and roads"),
    LANE_CHANGES("Lane Changes", "Safe lane changing techniques"),
    MOUNTAIN_DRIVING("Mountain Driving", "Driving on hills and mountain roads"),
    CONSTRUCTION_ZONES("Construction Zones", "Navigating work zones safely");

    private final String displayName;
    private final String description;

    Theme(String displayName, String description) {
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