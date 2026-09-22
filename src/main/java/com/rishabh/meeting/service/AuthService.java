package com.rishabh.meeting.service;

import com.rishabh.meeting.dto.LoginRequest;
import com.rishabh.meeting.dto.LoginResponse;
import com.rishabh.meeting.dto.RegisterRequest;
import com.rishabh.meeting.entity.Role;
import com.rishabh.meeting.entity.User;
import com.rishabh.meeting.exception.UserAlreadyExistsException;
import com.rishabh.meeting.repository.UserRepository;
import com.rishabh.meeting.security.JwtService;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository        userRepository;
    private final PasswordEncoder       passwordEncoder;
    private final JwtService            jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService          emailService;

    /**
     * Registers a new user with the USER role.
     * Returns a simple success message map; the caller wraps it in a ResponseEntity.
     */
    @Transactional
    public Map<String, String> register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        // Notify the new owner by email (async — does not block response)
        emailService.sendRegisterEmail(
                savedUser.getEmail(),
                savedUser.getName()
        );

        return Map.of("message", "User registered successfully");
    }

    /**
     * Registers a new user with the ADMIN role.
     * Can only be invoked by an existing ADMIN via the AdminController.
     */
    @Transactional
    public Map<String, String> createAdmin(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .city(request.getCity())
                .role(Role.ADMIN)
                .build();

        userRepository.save(user);

        return Map.of("message", "Admin registered successfully");
    }

    /**
     * Authenticates the user and returns a JWT + basic profile info.
     * Spring Security's AuthenticationManager handles BCrypt comparison internally.
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        // Throws BadCredentialsException if email/password doesn't match
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        String token = jwtService.generateToken(user);

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .role(user.getRole() != null ? user.getRole().name() : Role.USER.name())
                .build();
    }
}
