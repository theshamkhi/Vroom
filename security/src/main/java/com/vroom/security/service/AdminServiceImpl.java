package com.vroom.security.service;

import com.vroom.security.dto.AdminInstructorDTO;
import com.vroom.security.dto.AdminStatsDTO;
import com.vroom.security.dto.AdminUserDTO;
import com.vroom.security.model.entity.Instructor;
import com.vroom.security.model.entity.User;
import com.vroom.security.model.enums.Role;
import com.vroom.security.repository.InstructorRepository;
import com.vroom.security.repository.StudentRepository;
import com.vroom.security.repository.UserRepository;
import com.vroom.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final InstructorRepository instructorRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional(readOnly = true)
    public AdminStatsDTO getStats() {
        long totalUsers = userRepository.count();
        long totalStudents = userRepository.countByRole(Role.STUDENT);
        long totalInstructors = userRepository.countByRole(Role.INSTRUCTOR);
        long totalAdmins = userRepository.countByRole(Role.ADMIN);

        long activeUsers = userRepository.countByEnabledTrue();

        long pendingInstructorApprovals = instructorRepository.findAll().stream()
                .filter(i -> !i.isEnabled())
                .count();

        Double avgCompletion = studentRepository.getAverageCompletionPercentage();
        Long totalScenariosCompleted = studentRepository.getTotalScenariosCompleted();

        Double avgInstructorRating = instructorRepository.getAverageRatingAcrossAllInstructors();
        long totalActiveStudents = studentRepository.countByEnabledTrue();

        return AdminStatsDTO.builder()
                .totalUsers(totalUsers)
                .totalStudents(totalStudents)
                .totalInstructors(totalInstructors)
                .totalAdmins(totalAdmins)
                .activeUsers(activeUsers)
                .pendingInstructors(pendingInstructorApprovals)
                .averageStudentCompletionPercentage(avgCompletion)
                .totalScenariosCompleted(totalScenariosCompleted)
                .averageInstructorRating(avgInstructorRating)
                .totalActiveStudents(totalActiveStudents)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminUserDTO> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toAdminUserDTO);
    }

    @Override
    @Transactional
    public AdminUserDTO updateUserRole(UUID userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setRole(role);
        User saved = userRepository.save(user);
        return toAdminUserDTO(saved);
    }

    @Override
    @Transactional
    public AdminUserDTO updateUserStatus(UUID userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setEnabled(enabled);
        User saved = userRepository.save(user);
        return toAdminUserDTO(saved);
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminInstructorDTO> getInstructors(Pageable pageable) {
        return instructorRepository.findAll(pageable).map(this::toAdminInstructorDTO);
    }

    @Override
    @Transactional
    public AdminInstructorDTO setInstructorApproved(UUID instructorId, boolean approved) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor", "id", instructorId));

        instructor.setEnabled(approved);
        Instructor saved = instructorRepository.save(instructor);
        return toAdminInstructorDTO(saved);
    }

    private AdminUserDTO toAdminUserDTO(User user) {
        return AdminUserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }

    private AdminInstructorDTO toAdminInstructorDTO(Instructor instructor) {
        return AdminInstructorDTO.builder()
                .id(instructor.getId())
                .email(instructor.getEmail())
                .firstName(instructor.getFirstName())
                .lastName(instructor.getLastName())
                .fullName(instructor.getFullName())
                .enabled(instructor.isEnabled())
                .createdAt(instructor.getCreatedAt())
                .licenseNumber(instructor.getLicenseNumber())
                .drivingSchool(instructor.getDrivingSchool())
                .yearsOfExperience(instructor.getYearsOfExperience())
                .joinDate(instructor.getJoinDate())
                .averageRating(instructor.getAverageRating())
                .totalRatings(instructor.getTotalRatings())
                .activeStudents(instructor.getActiveStudents())
                .totalStudentsTaught(instructor.getTotalStudentsTaught())
                .availableForNewStudents(instructor.getAvailableForNewStudents())
                .maxStudents(instructor.getMaxStudents())
                .build();
    }
}
