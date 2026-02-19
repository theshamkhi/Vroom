package com.vroom.content.service;

import com.vroom.content.dto.BadgeDTO;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for badge management operations
 */
public interface BadgeService {

    List<BadgeDTO> getAllActiveBadges();

    BadgeDTO getBadgeById(UUID id);

    List<BadgeDTO> getStudentBadges(UUID studentId);

    BadgeDTO awardBadgeToStudent(
            UUID studentId,
            UUID badgeId,
            String earnedDescription,
            UUID scenarioId,
            String studentName,
            String studentEmail
    );
}
