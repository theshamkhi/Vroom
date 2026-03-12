package com.vroom.security.service;

import com.vroom.security.dto.AdminInstructorDTO;
import com.vroom.security.dto.AdminStatsDTO;
import com.vroom.security.dto.AdminUserDTO;
import com.vroom.security.model.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AdminService {
    AdminStatsDTO getStats();

    Page<AdminUserDTO> getUsers(Pageable pageable);

    AdminUserDTO updateUserRole(UUID userId, Role role);

    AdminUserDTO updateUserStatus(UUID userId, boolean enabled);

    void deleteUser(UUID userId);

    Page<AdminInstructorDTO> getInstructors(Pageable pageable);

    AdminInstructorDTO setInstructorApproved(UUID instructorId, boolean approved);
}
