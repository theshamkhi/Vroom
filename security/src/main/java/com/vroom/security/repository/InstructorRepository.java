package com.vroom.security.repository;

import com.vroom.security.model.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Instructor entity operations
 */
@Repository
public interface InstructorRepository extends JpaRepository<Instructor, UUID> {

    /**
     * Find instructor by email (inherited from User)
     */
    Optional<Instructor> findByEmailIgnoreCase(String email);

    /**
     * Find instructor by license number
     */
    Optional<Instructor> findByLicenseNumber(String licenseNumber);

    /**
     * Find instructors by driving school
     */
    List<Instructor> findByDrivingSchool(String drivingSchool);

    /**
     * Find instructors by specialty
     */
    List<Instructor> findBySpecialty(String specialty);

    /**
     * Find available instructors (accepting new students)
     */
    List<Instructor> findByAvailableForNewStudentsTrue();

    /**
     * Find instructors with available slots
     */
    @Query("SELECT i FROM Instructor i WHERE i.activeStudents < i.maxStudents AND i.availableForNewStudents = true")
    List<Instructor> findInstructorsWithAvailableSlots();

    /**
     * Find top-rated instructors
     */
    @Query("SELECT i FROM Instructor i WHERE i.averageRating IS NOT NULL ORDER BY i.averageRating DESC")
    List<Instructor> findTopRatedInstructors();

    /**
     * Find instructors by minimum rating
     */
    @Query("SELECT i FROM Instructor i WHERE i.averageRating >= :minRating")
    List<Instructor> findByMinimumRating(@Param("minRating") Double minRating);

    /**
     * Find instructors by years of experience (minimum)
     */
    @Query("SELECT i FROM Instructor i WHERE i.yearsOfExperience >= :minYears")
    List<Instructor> findByMinimumExperience(@Param("minYears") Integer minYears);

    /**
     * Find instructors who speak a specific language
     */
    @Query("SELECT i FROM Instructor i WHERE i.languagesSpoken LIKE %:language%")
    List<Instructor> findByLanguage(@Param("language") String language);

    /**
     * Count instructors by driving school
     */
    long countByDrivingSchool(String drivingSchool);

    /**
     * Count available instructors
     */
    long countByAvailableForNewStudentsTrue();

    /**
     * Get average rating across all instructors
     */
    @Query("SELECT AVG(i.averageRating) FROM Instructor i WHERE i.averageRating IS NOT NULL")
    Double getAverageRatingAcrossAllInstructors();

    /**
     * Get total number of active students across all instructors
     */
    @Query("SELECT SUM(i.activeStudents) FROM Instructor i")
    Long getTotalActiveStudents();

    /**
     * Find instructors with capacity for more students
     */
    @Query("SELECT i FROM Instructor i WHERE (i.maxStudents - i.activeStudents) >= :minSlots")
    List<Instructor> findWithMinimumAvailableSlots(@Param("minSlots") Integer minSlots);

    /**
     * Check if license number exists
     */
    boolean existsByLicenseNumber(String licenseNumber);

    /**
     * Find instructors by school and specialty
     */
    List<Instructor> findByDrivingSchoolAndSpecialty(String drivingSchool, String specialty);

    /**
     * Find instructors sorted by total students taught
     */
    @Query("SELECT i FROM Instructor i ORDER BY i.totalStudentsTaught DESC")
    List<Instructor> findMostExperiencedInstructors();
}