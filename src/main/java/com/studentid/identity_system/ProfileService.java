package com.studentid.identity_system;

import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

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

        StudentProfile profile = new StudentProfile();
        profile.setFullName(request.getFullName());
        profile.setDepartment(request.getDepartment());
        profile.setYear(request.getYear());
        profile.setUser(user);

        user.setProfileCompleted(true);
        userRepository.save(user);
        profileRepository.save(profile);
    }

    public ProfileResponse getProfile(String userId) {
        StudentProfile profile = profileRepository
                .findByUserId(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return new ProfileResponse(
                profile.getFullName(),
                profile.getDepartment(),
                profile.getYear()
        );
    }

    public ProfileResponse updateProfile(String userId, UpdateProfileRequest request) {

        StudentProfile profile = profileRepository
                .findByUserId(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        System.out.println("Before: " + profile.getYear());

        mapper.updateProfileFromDto(request, profile);

        System.out.println("After: " + profile.getYear());

        profileRepository.save(profile);

        return mapper.toResponse(profile);
    }
}