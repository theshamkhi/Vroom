package com.vroom.learning.service;

import com.vroom.content.service.BadgeService;
import com.vroom.content.model.entity.Answer;
import com.vroom.content.model.entity.Question;
import com.vroom.content.repository.AnswerRepository;
import com.vroom.content.repository.BadgeRepository;
import com.vroom.content.repository.StudentBadgeRepository;
import com.vroom.content.repository.QuestionRepository;
import com.vroom.learning.dto.ProgressDTO;
import com.vroom.learning.dto.SubmitAnswerRequest;
import com.vroom.learning.dto.SubmitAnswerResponse;
import com.vroom.learning.model.entity.StudentAnswer;
import com.vroom.learning.model.entity.StudentScenario;
import com.vroom.learning.repository.StudentAnswerRepository;
import com.vroom.learning.repository.StudentScenarioRepository;
import com.vroom.security.model.entity.Student;
import com.vroom.security.repository.StudentRepository;
import com.vroom.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for tracking student progress
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressService {

    private final StudentScenarioRepository studentScenarioRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    private final StudentRepository studentRepository;
    private final BadgeRepository badgeRepository;
    private final StudentBadgeRepository studentBadgeRepository;
    private final BadgeService badgeService;

    private static final String BADGE_POINTS_100 = "Points 100";
    private static final String BADGE_POINTS_300 = "Points 300";
    private static final String BADGE_POINTS_600 = "Points 600";
    private static final String BADGE_POINTS_1000 = "Points 1000";
    private static final String BADGE_POINTS_1500 = "Points 1500";

    /**
     * Start a scenario for a student
     */
    @Transactional
    public ProgressDTO startScenario(UUID studentId, UUID scenarioId) {
        log.info("Student {} starting scenario {}", studentId, scenarioId);

        StudentScenario progress = studentScenarioRepository
                .findByStudentIdAndScenarioId(studentId, scenarioId)
                .orElseGet(() -> StudentScenario.builder()
                        .studentId(studentId)
                        .scenarioId(scenarioId)
                        .build());

        progress.startScenario();
        progress = studentScenarioRepository.save(progress);

        return mapToDTO(progress);
    }

    /**
     * Submit answer to a question
     */
    @Transactional
    public SubmitAnswerResponse submitAnswer(UUID studentId, SubmitAnswerRequest request) {
        log.info("Student {} submitting answer for question {}", studentId, request.getQuestionId());

        // Get or create student scenario progress
        StudentScenario progress = studentScenarioRepository
                .findByStudentIdAndScenarioId(studentId, request.getScenarioId())
                .orElseThrow(() -> new ResourceNotFoundException("StudentScenario", "scenarioId", request.getScenarioId()));

        // Get question with answers
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", request.getQuestionId()));

        // Get correct answer IDs
        Set<UUID> correctAnswerIds = question.getAnswers().stream()
                .filter(Answer::getIsCorrect)
                .map(Answer::getId)
                .collect(Collectors.toSet());

        // Validate answer
        boolean isCorrect = correctAnswerIds.equals(request.getSelectedAnswerIds());
        int pointsEarned = isCorrect ? question.getPoints() : 0;

        // Save student answer
        StudentAnswer answer = StudentAnswer.builder()
                .studentId(studentId)
                .questionId(request.getQuestionId())
                .scenarioId(request.getScenarioId())
                .studentScenarioId(progress.getId())
                .selectedAnswerIds(request.getSelectedAnswerIds())
                .isCorrect(isCorrect)
                .pointsEarned(pointsEarned)
                .timeTakenSeconds(request.getTimeTakenSeconds())
                .hintUsed(request.getHintUsed() != null ? request.getHintUsed() : false)
                .build();

        studentAnswerRepository.save(answer);
        log.info("Answer submitted for question {} - Correct: {}, Points: {}",
                request.getQuestionId(), isCorrect, pointsEarned);

        return SubmitAnswerResponse.builder()
                .correct(isCorrect)
                .pointsEarned(pointsEarned)
                .explanation(null)
                .build();
    }

    /**
     * Complete a scenario
     */
    @Transactional
    public ProgressDTO completeScenario(UUID studentId, UUID scenarioId, Double score, Integer pointsEarned) {
        log.info("Student {} completing scenario {} with score {}", studentId, scenarioId, score);

        StudentScenario progress = studentScenarioRepository
                .findByStudentIdAndScenarioId(studentId, scenarioId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentScenario", "scenarioId", scenarioId));

        // Get answer statistics
        long correctAnswers = studentAnswerRepository
                .countByStudentIdAndScenarioIdAndIsCorrectTrue(studentId, scenarioId);
        long totalQuestions = studentAnswerRepository
                .countByStudentIdAndScenarioId(studentId, scenarioId);

        progress.completeScenario(score, pointsEarned, (int) correctAnswers, (int) totalQuestions);
        progress = studentScenarioRepository.save(progress);

        updateStudentTotalPointsAndAwardPointBadges(studentId);

        log.info("Scenario completed successfully");
        return mapToDTO(progress);
    }

    private void updateStudentTotalPointsAndAwardPointBadges(UUID studentId) {
        Integer totalPoints = studentScenarioRepository.getTotalPointsByStudent(studentId);
        if (totalPoints == null) {
            totalPoints = 0;
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        student.setTotalPoints(totalPoints);
        studentRepository.save(student);

        awardIfThresholdReached(student, totalPoints, 100, BADGE_POINTS_100);
        awardIfThresholdReached(student, totalPoints, 300, BADGE_POINTS_300);
        awardIfThresholdReached(student, totalPoints, 600, BADGE_POINTS_600);
        awardIfThresholdReached(student, totalPoints, 1000, BADGE_POINTS_1000);
        awardIfThresholdReached(student, totalPoints, 1500, BADGE_POINTS_1500);

        long badgeCount = studentBadgeRepository.countByStudentId(studentId);
        student.setBadgesEarned((int) badgeCount);
        studentRepository.save(student);
    }

    private void awardIfThresholdReached(Student student, int totalPoints, int threshold, String badgeName) {
        if (totalPoints < threshold) {
            return;
        }

        var badgeOpt = badgeRepository.findByName(badgeName);
        if (badgeOpt.isEmpty()) {
            log.warn("Points badge '{}' not found in DB. Threshold reached: {} points for student {}", badgeName, totalPoints, student.getId());
            return;
        }

        var badge = badgeOpt.get();

        if (studentBadgeRepository.existsByStudentIdAndBadgeId(student.getId(), badge.getId())) {
            return;
        }

        badgeService.awardBadgeToStudent(
                student.getId(),
                badge.getId(),
                "Reached " + threshold + " points",
                null,
                student.getFullName(),
                student.getEmail()
        );
    }

    /**
     * Get student's progress on all scenarios
     */
    public List<ProgressDTO> getStudentProgress(UUID studentId) {
        log.debug("Fetching progress for student: {}", studentId);

        return studentScenarioRepository.findByStudentIdOrderByLastAccessedAtDesc(studentId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Get student's progress on specific scenario
     */
    public ProgressDTO getScenarioProgress(UUID studentId, UUID scenarioId) {
        log.debug("Fetching progress for student {} on scenario {}", studentId, scenarioId);

        StudentScenario progress = studentScenarioRepository
                .findByStudentIdAndScenarioId(studentId, scenarioId)
                .orElse(null);

        return progress != null ? mapToDTO(progress) : null;
    }

    /**
     * Map entity to DTO
     */
    private ProgressDTO mapToDTO(StudentScenario progress) {
        return ProgressDTO.builder()
                .id(progress.getId())
                .studentId(progress.getStudentId())
                .scenarioId(progress.getScenarioId())
                .status(progress.getStatus())
                .attemptCount(progress.getAttemptCount())
                .highestScore(progress.getHighestScore())
                .latestScore(progress.getLatestScore())
                .averageScore(progress.getAverageScore())
                .totalPointsEarned(progress.getTotalPointsEarned())
                .totalPossiblePoints(progress.getTotalPossiblePoints())
                .timeSpentSeconds(progress.getTimeSpentSeconds())
                .formattedTimeSpent(progress.getFormattedTimeSpent())
                .correctAnswers(progress.getCorrectAnswers())
                .totalQuestions(progress.getTotalQuestions())
                .completionPercentage(progress.getCompletionPercentage())
                .startedAt(progress.getStartedAt())
                .completedAt(progress.getCompletedAt())
                .lastAccessedAt(progress.getLastAccessedAt())
                .build();
    }
}