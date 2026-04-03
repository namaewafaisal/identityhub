package com.studentid.identity_system;

import java.io.ByteArrayInputStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    public void createProfile(@Valid @RequestBody ProfileRequest request,
                               Authentication authentication) {

        String userId = (String) authentication.getPrincipal();
        profileService.createProfile(request, userId);
    }

    @GetMapping
    public ProfileResponse getProfile(Authentication authentication) {

        String userId = (String) authentication.getPrincipal();

        return profileService.getProfile(userId);
    }

    @PatchMapping
    public ProfileResponse updateProfile(@Valid @RequestBody UpdateProfileRequest request,
                            Authentication authentication) {

        String userId = (String) authentication.getPrincipal();

        return profileService.updateProfile(userId, request);
    }
    @GetMapping("/search")
    public ProfileResponse searchByRegisterNumber(@RequestParam String registerNumber) {

        return profileService.getProfileByRegisterNumber(registerNumber);
    }

    @GetMapping("/filter")
    public Page<ProfileResponse> filter(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String name,
            @PageableDefault(page = 0, size = 20) Pageable pageable
    ) {
        return profileService.filter(department, year,name,pageable);
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> export(@RequestBody ExportRequest request) {

        byte[] excel = profileService.export(request);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=profiles.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(excel);
    }
}