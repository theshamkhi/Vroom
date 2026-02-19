package com.vroom.content.service;

import com.vroom.content.dto.BadgeDTO;
import com.vroom.content.model.entity.Badge;
import com.vroom.content.model.entity.StudentBadge;
import com.vroom.content.repository.BadgeRepository;
import com.vroom.content.repository.StudentBadgeRepository;
import com.vroom.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for badge management operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BadgeServiceImpl implements BadgeService {

    private final BadgeRepository badgeRepository;
    private final StudentBadgeRepository studentBadgeRepository;
    private final EmailService emailService;

    /**
     * Get all active badges
     */
    public List<BadgeDTO> getAllActiveBadges() {
        log.debug("Fetching all active badges");

        return badgeRepository.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Get badge by ID
     */
    public BadgeDTO getBadgeById(UUID id) {
        log.debug("Fetching badge: {}", id);

        Badge badge = badgeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Badge not found with id: " + id));

        return mapToDTO(badge);
    }

    /**
     * Get badges earned by student
     */
    public List<BadgeDTO> getStudentBadges(UUID studentId) {
        log.debug("Fetching badges for student: {}", studentId);

        return studentBadgeRepository.findByStudentIdOrderByEarnedAtDesc(studentId)
                .stream()
                .map(this::mapStudentBadgeToDTO)
                .toList();
    }

    /**
     * Award badge to student
     */
    @Transactional
    public BadgeDTO awardBadgeToStudent(UUID studentId, UUID badgeId, String earnedDescription, UUID scenarioId, String studentName, String studentEmail) {
        log.info("Awarding badge {} to student {}", badgeId, studentId);

        // Check if student already has this badge
        if (studentBadgeRepository.existsByStudentIdAndBadgeId(studentId, badgeId)) {
            throw new RuntimeException("Student already has this badge");
        }

        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new RuntimeException("Badge not found with id: " + badgeId));

        // Create student badge
        StudentBadge studentBadge = StudentBadge.builder()
                .studentId(studentId)
                .badgeId(badgeId)
                .earnedAt(LocalDateTime.now())
                .earnedDescription(earnedDescription)
                .earnedInScenarioId(scenarioId)
                .notified(false)
                .displayed(true)
                .build();

        studentBadgeRepository.save(studentBadge);

        // Update badge earned count
        badge.incrementEarnedCount();
        badgeRepository.save(badge);

        // Send notification email
        try {
            emailService.sendBadgeEarnedEmail(
                    studentEmail,
                    studentName,
                    badge.getName(),
                    badge.getDescription()
            );
            studentBadge.markAsNotified();
            studentBadgeRepository.save(studentBadge);
        } catch (Exception e) {
            log.error("Failed to send badge earned email", e);
        }

        log.info("Badge awarded successfully to student {}", studentId);

        BadgeDTO dto = mapToDTO(badge);
        dto.setEarnedAt(studentBadge.getEarnedAt());
        dto.setEarnedDescription(studentBadge.getEarnedDescription());
        dto.setIsEarned(true);

        return dto;
    }

    /**
     * Map entity to DTO
     */
    private BadgeDTO mapToDTO(Badge badge) {
        return BadgeDTO.builder()
                .id(badge.getId())
                .name(badge.getName())
                .description(badge.getDescription())
                .type(badge.getType())
                .iconUrl(badge.getIconUrl())
                .criteria(badge.getCriteria())
                .pointsValue(badge.getPointsValue())
                .active(badge.getActive())
                .earnedCount(badge.getEarnedCount())
                .relatedScenarioId(badge.getRelatedScenarioId())
                .relatedTheme(badge.getRelatedTheme())
                .createdAt(badge.getCreatedAt())
                .isEarned(false)
                .build();
    }

    private BadgeDTO mapStudentBadgeToDTO(StudentBadge studentBadge) {
        Badge badge = badgeRepository.findById(studentBadge.getBadgeId())
                .orElseThrow(() -> new RuntimeException("Badge not found"));

        BadgeDTO dto = mapToDTO(badge);
        dto.setEarnedAt(studentBadge.getEarnedAt());
        dto.setEarnedDescription(studentBadge.getEarnedDescription());
        dto.setIsEarned(true);

        return dto;
    }
}