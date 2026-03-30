package com.studentid.identity_system;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    public void createProfile(@RequestBody ProfileRequest request,
                               Authentication authentication) {

        String userId = (String) authentication.getPrincipal();

        profileService.createProfile(request, userId);
    }
}