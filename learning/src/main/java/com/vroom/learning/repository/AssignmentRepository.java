package com.vroom.learning.repository;

import com.vroom.learning.model.entity.Assignment;
import com.vroom.learning.model.enums.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Repository for Assignment entity operations
 */
@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {

    /**
     * Find assignments for a student
     */
    List<Assignment> findByStudentIdOrderByDueDateAsc(UUID studentId);

    /**
     * Find assignments created by instructor
     */
    List<Assignment> findByInstructorIdOrderByCreatedAtDesc(UUID instructorId);


    List<Assignment> findByStudentId(UUID studentId);
}