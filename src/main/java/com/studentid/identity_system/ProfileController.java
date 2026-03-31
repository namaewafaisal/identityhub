package com.studentid.identity_system;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

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
}