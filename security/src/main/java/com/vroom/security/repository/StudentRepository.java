package com.vroom.security.repository;

import com.vroom.security.model.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Student entity operations
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {

    /**
     * Find student by email (inherited from User)
     */
    Optional<Student> findByEmailIgnoreCase(String email);

    /**
     * Find all students assigned to a specific instructor
     */
    List<Student> findByAssignedInstructorId(UUID instructorId);

    /**
     * Find students by driving school
     */
    List<Student> findByDrivingSchool(String drivingSchool);

    /**
     * Find students by current level
     */
    List<Student> findByCurrentLevel(String level);

    /**
     * Find students by completion percentage range
     */
    @Query("SELECT s FROM Student s WHERE s.completionPercentage >= :min AND s.completionPercentage <= :max")
    List<Student> findByCompletionPercentageRange(@Param("min") Double min, @Param("max") Double max);

    /**
     * Find top performing students (by total points)
     */
    @Query("SELECT s FROM Student s ORDER BY s.totalPoints DESC")
    List<Student> findTopPerformers();

    /**
     * Find students enrolled after a specific date
     */
    List<Student> findByEnrollmentDateAfter(LocalDate date);

    /**
     * Find students with completion deadline approaching
     */
    @Query("SELECT s FROM Student s WHERE s.targetCompletionDate BETWEEN :start AND :end")
    List<Student> findStudentsWithApproachingDeadline(@Param("start") LocalDate start, @Param("end") LocalDate end);

    /**
     * Find students by permit number
     */
    Optional<Student> findByPermitNumber(String permitNumber);

    /**
     * Count students by level
     */
    long countByCurrentLevel(String level);

    /**
     * Count students assigned to instructor
     */
    long countByAssignedInstructorId(UUID instructorId);

    /**
     * Get average completion percentage across all students
     */
    @Query("SELECT AVG(s.completionPercentage) FROM Student s")
    Double getAverageCompletionPercentage();

    /**
     * Find students with no assigned instructor
     */
    List<Student> findByAssignedInstructorIdIsNull();

    /**
     * Find students by preferred language
     */
    List<Student> findByPreferredLanguage(String language);

    /**
     * Find students who have earned a specific number of badges or more
     */
    @Query("SELECT s FROM Student s WHERE s.badgesEarned >= :minBadges")
    List<Student> findByMinimumBadges(@Param("minBadges") Integer minBadges);

    /**
     * Get total number of scenarios completed by all students
     */
    @Query("SELECT SUM(s.scenariosCompleted) FROM Student s")
    Long getTotalScenariosCompleted();

    /**
     * Find inactive students (low completion percentage and no recent activity)
     */
    @Query("SELECT s FROM Student s WHERE s.completionPercentage < :threshold AND s.lastLoginAt < :lastActivity")
    List<Student> findInactiveStudents(@Param("threshold") Double threshold, @Param("lastActivity") java.time.LocalDateTime lastActivity);
}