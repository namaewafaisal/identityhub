package com.studentid.identity_system;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final StudentProfileRepository profileRepository;
    private final UserRepository userRepository;

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

        profileRepository.save(profile);
    }
}