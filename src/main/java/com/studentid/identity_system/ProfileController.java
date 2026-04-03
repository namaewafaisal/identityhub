package com.studentid.identity_system;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            Pageable pageable
    ) {
        return profileService.filter(department, year,name,pageable);
    }
}