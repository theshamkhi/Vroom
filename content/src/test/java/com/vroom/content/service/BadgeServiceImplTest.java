package com.vroom.content.service;

import com.vroom.content.model.entity.Badge;
import com.vroom.content.model.enums.BadgeType;
import com.vroom.content.repository.BadgeRepository;
import com.vroom.content.repository.StudentBadgeRepository;
import com.vroom.notification.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BadgeServiceImplTest {

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private StudentBadgeRepository studentBadgeRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private BadgeServiceImpl service;

    private UUID studentId;
    private UUID badgeId;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        badgeId = UUID.randomUUID();
    }

    @Test
    void awardBadgeToStudent_whenStudentAlreadyHasBadge_throwsRuntimeException() {
        when(studentBadgeRepository.existsByStudentIdAndBadgeId(studentId, badgeId)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> service.awardBadgeToStudent(
                studentId,
                badgeId,
                "desc",
                UUID.randomUUID(),
                "Student",
                "student@example.com"
        ));

        verify(badgeRepository, never()).findById(any());
        verify(studentBadgeRepository, never()).save(any());
    }

    @Test
    void awardBadgeToStudent_whenEmailSendFails_stillAwardsAndReturnsEarnedDto() {
        UUID scenarioId = UUID.randomUUID();

        when(studentBadgeRepository.existsByStudentIdAndBadgeId(studentId, badgeId)).thenReturn(false);

        Badge badge = Badge.builder()
                .id(badgeId)
                .name("Highway Master")
                .description("Mastered highway driving")
                .type(BadgeType.SKILL_MASTERY)
                .earnedCount(0)
                .active(true)
                .build();

        when(badgeRepository.findById(badgeId)).thenReturn(Optional.of(badge));

        when(studentBadgeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(badgeRepository.save(any(Badge.class))).thenAnswer(inv -> inv.getArgument(0));

        doThrow(new RuntimeException("SMTP down"))
                .when(emailService)
                .sendBadgeEarnedEmail(any(), any(), any(), any());

        var dto = service.awardBadgeToStudent(
                studentId,
                badgeId,
                "Completed scenario",
                scenarioId,
                "Student Name",
                "student@example.com"
        );

        assertTrue(dto.getIsEarned());
        assertEquals(badgeId, dto.getId());
        assertEquals(1, dto.getEarnedCount());
        assertNotNull(dto.getEarnedAt());
        assertEquals("Completed scenario", dto.getEarnedDescription());

        verify(studentBadgeRepository, times(1)).save(any());
        verify(badgeRepository, times(1)).save(badge);
        verify(emailService, times(1)).sendBadgeEarnedEmail(any(), any(), any(), any());
    }
}
