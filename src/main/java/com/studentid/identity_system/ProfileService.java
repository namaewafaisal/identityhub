package com.studentid.identity_system;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.studentid.identity_system.dto.ExportRequest;
import com.studentid.identity_system.dto.ProfileRequest;
import com.studentid.identity_system.dto.ProfileResponse;
import com.studentid.identity_system.dto.UpdateProfileRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {   

    private final StudentProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ProfileMapper mapper;

    @Value("${app.pagination.max-size}")
    private int maxSize;

    private final Map<ProfileField, Function<StudentProfile, Object>> fieldExtractors =
            createFieldExtractors();

    private Map<ProfileField, Function<StudentProfile, Object>> createFieldExtractors() {
        Map<ProfileField, Function<StudentProfile, Object>> map = new LinkedHashMap<>();

        map.put(ProfileField.REGISTER_NUMBER, StudentProfile::getRegisterNumber);
        map.put(ProfileField.FULL_NAME, StudentProfile::getFullName);
        map.put(ProfileField.DEPARTMENT, StudentProfile::getDepartment);
        map.put(ProfileField.YEAR, StudentProfile::getYear);
        map.put(ProfileField.SECTION, StudentProfile::getSection);
        map.put(ProfileField.BATCH, StudentProfile::getBatch);

        map.put(ProfileField.GITHUB, p -> p.getHandle() != null ? p.getHandle().getGithub() : "");
        map.put(ProfileField.LEETCODE, p -> p.getHandle() != null ? p.getHandle().getLeetcode() : "");
        map.put(ProfileField.LINKEDIN, p -> p.getHandle() != null ? p.getHandle().getLinkedin() : "");
        map.put(ProfileField.HACKERRANK, p -> p.getHandle() != null ? p.getHandle().getHackerrank() : "");
        map.put(ProfileField.CODE_FORCES, p -> p.getHandle() != null ? p.getHandle().getCodeforces() : "");

        return map;
    }

    private final Map<ProfileField, String> fieldHeaders = Map.ofEntries(
        Map.entry(ProfileField.REGISTER_NUMBER, "Register Number"),
        Map.entry(ProfileField.FULL_NAME, "Full Name"),
        Map.entry(ProfileField.DEPARTMENT, "Department"),
        Map.entry(ProfileField.YEAR, "Year"),
        Map.entry(ProfileField.BATCH, "Batch"),
        Map.entry(ProfileField.SECTION, "Section"),
        Map.entry(ProfileField.GITHUB, "GitHub"),
        Map.entry(ProfileField.LEETCODE, "LeetCode"),
        Map.entry(ProfileField.HACKERRANK, "Hackerrank"),
        Map.entry(ProfileField.CODE_FORCES, "Code Forces"),
        Map.entry(ProfileField.LINKEDIN, "Linkedin")
    );
    @Transactional
    public void createProfile(ProfileRequest request, String userId) {

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not Found"));

        // check if profile already exists
        if (profileRepository.findByUserId(user.getId()).isPresent()) {
            throw new ResourceAlreadyExistsException("Profile already exists");
        }
        if (profileRepository.existsByRegisterNumber(request.getRegisterNumber())) {
            throw new ResourceAlreadyExistsException("Register Number already exists");
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
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        return mapper.toResponse(profile);
    }
    @Transactional
    public ProfileResponse updateProfile(String userId, UpdateProfileRequest request) {

        StudentProfile profile = profileRepository
                .findByUserId(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

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
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return mapper.toResponse(profile);
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

        

        pageable = PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize() > maxSize ? maxSize : pageable.getPageSize(),
            pageable.getSort().isUnsorted() ? Sort.by("registerNumber").ascending() : pageable.getSort()
        );

        Page<StudentProfile> page = profileRepository.findAll(spec,pageable);

        return page.map(mapper::toResponse);
    }
    
    public byte[] export(ExportRequest request) {

        List<ProfileField> fields = request.getFields();

        if (fields == null) {
            // ALL fields
            fields = new ArrayList<>(fieldExtractors.keySet());
        } else if (fields.isEmpty()) {
            throw new BadRequestException("Fields cannot be empty");
        }

        // 2. Base filter (MANDATORY)
        Specification<StudentProfile> spec =
                ProfileSpecifications.hasDepartment(request.getDepartment())
                .and(ProfileSpecifications.hasYear(request.getYear()));

        // 3. Optional filter
        if (request.getSection() != null && !request.getSection().isBlank()) {
            spec = spec.and(ProfileSpecifications.hasSection(request.getSection()));
        }

        List<StudentProfile> profiles = 
            profileRepository.findAll(spec, Sort.by("registerNumber").ascending());
        if (profiles.isEmpty()) {
            throw new ResourceNotFoundException("No students found for given filters");
        }

        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Profiles");
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            // Header
            Row header = sheet.createRow(0);
            for (int i = 0; i < fields.size(); i++) {
                ProfileField field = fields.get(i);
                String headerName = fieldHeaders.getOrDefault(field, field.name());

                Cell cell = header.createCell(i);
                cell.setCellValue(headerName);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;

            for (StudentProfile profile : profiles) {

                Row row = sheet.createRow(rowNum++);

                for (int i = 0; i < fields.size(); i++) {

                    ProfileField field = fields.get(i);

                    Function<StudentProfile, Object> extractor = fieldExtractors.get(field);

                    Object value = extractor.apply(profile);

                    row.createCell(i).setCellValue(
                            value != null ? value.toString() : ""
                    );
                }
            }
            for (int i = 0; i < fields.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            workbook.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to export Excel");
        }
    }
}