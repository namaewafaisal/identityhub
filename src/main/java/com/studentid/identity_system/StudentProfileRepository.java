package com.studentid.identity_system;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long>, JpaSpecificationExecutor<StudentProfile> {

    Optional<StudentProfile> findByUserId(UUID userId);

    boolean existsByRegisterNumber(String registerNumber);
    Optional<StudentProfile> findByRegisterNumber(String registerNumber);

    List<StudentProfile> findByDepartmentAndYear(String department, Integer year);


}