package com.studentid.identity_system;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class ProfileService {   

    private final StudentProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ProfileMapper mapper;

    @Transactional
    public void createProfile(ProfileRequest request, String userId) {

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        // check if profile already exists
        if (profileRepository.findByUserId(user.getId()).isPresent()) {
            throw new RuntimeException("Profile already exists");
        }
        if (profileRepository.existsByRegisterNumber(request.getRegisterNumber())) {
        throw new RuntimeException("Register number already exists");
    }

        StudentProfile profile = mapper.toEntity(request);
        profile.setUser(user);
        user.setProfileCompleted(true);

        if (request.getHandle() != null) {
            StudentHandle handle = mapper.toHandleEntity(request.getHandle());
            handle.setProfile(profile);
            profile.setHandle(handle);
        }
        userRepository.save(user);
        profileRepository.save(profile);

    }

    public ProfileResponse getProfile(String userId) {
        StudentProfile profile = profileRepository
                .findByUserId(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return mapper.toResponse(profile);
    }
    @Transactional
    public ProfileResponse updateProfile(String userId, UpdateProfileRequest request) {

        StudentProfile profile = profileRepository
                .findByUserId(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        mapper.updateProfileFromDto(request, profile);
        if (request.getHandle() != null) {

            if (profile.getHandle() == null) {
                // create
                StudentHandle handle = mapper.toHandleEntity(request.getHandle());
                handle.setProfile(profile);
                profile.setHandle(handle);

            } else {
                // update using mapper
                mapper.updateHandleFromDto(request.getHandle(), profile.getHandle());
            }
        }

        profileRepository.save(profile);

        return mapper.toResponse(profile);
    }
    public ProfileResponse getProfileByRegisterNumber(String registerNumber) {

        StudentProfile profile = profileRepository
                .findByRegisterNumber(registerNumber)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return mapper.toResponse(profile);
    }

    public List<ProfileResponse> filterByDepartmentAndYear(String department, Integer year) {

        List<StudentProfile> profiles =
                profileRepository.findByDepartmentAndYear(department, year);

        return profiles.stream()
                .map(mapper::toResponse)
                .toList();
    }

    public Page<ProfileResponse> filter(String department, Integer year,String name, Pageable pageable) {

        Specification<StudentProfile> spec = (root, query, cb) -> null;     
        if (department != null && !department.isBlank()) {
            spec = spec.and(ProfileSpecifications.hasDepartment(department));
        }

        if (year != null) {
            spec = spec.and(ProfileSpecifications.hasYear(year));
        }
        if (name != null && !name.isBlank()) {
            spec = spec.and(ProfileSpecifications.nameContains(name));
        }

        Page<StudentProfile> page = profileRepository.findAll(spec,pageable);

        return page.map(mapper::toResponse);
    }
    public ByteArrayInputStream exportToExcel() {

        List<StudentProfile> profiles = profileRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Profiles");

            // Header
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Register Number");
            header.createCell(1).setCellValue("Full Name");
            header.createCell(2).setCellValue("Department");
            header.createCell(3).setCellValue("Year");
            header.createCell(4).setCellValue("Batch");
            header.createCell(5).setCellValue("GitHub");
            header.createCell(6).setCellValue("LeetCode");

            int rowIdx = 1;

            for (StudentProfile profile : profiles) {

                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(profile.getRegisterNumber());
                row.createCell(1).setCellValue(profile.getFullName());
                row.createCell(2).setCellValue(profile.getDepartment());
                row.createCell(3).setCellValue(profile.getYear());
                row.createCell(4).setCellValue(profile.getBatch());

                // handle (important)
                if (profile.getHandle() != null) {
                    row.createCell(5).setCellValue(profile.getHandle().getGithub());
                    row.createCell(6).setCellValue(profile.getHandle().getLeetcode());
                }
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Failed to export Excel");
        }
    }
}