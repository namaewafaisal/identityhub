package com.studentid.identity_system;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public void register(AuthRequest request) {

        validateCollegeEmail(request.getEmail());

        // 1. Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // 2. Create user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // IMPORTANT
        user.setRole(Role.STUDENT);
        user.setVerified(false);
        user.setProfileCompleted(false);

        // 3. Save
        userRepository.save(user);
    }

    private void validateCollegeEmail(String email) {
        if (!email.endsWith("@trp.srmtrichy.edu.in")) {
            throw new RuntimeException("Only college email addresses are allowed");
        }

    }
    
    public AuthResponse login(AuthRequest request) {

        // 1. Find user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // 2. Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // 3. Generate token (we'll implement next)
        String token = jwtUtil.generateToken(user);

        return new AuthResponse(
                token,
                user.getRole().name(),
                user.getId().toString()
        );
    }
}