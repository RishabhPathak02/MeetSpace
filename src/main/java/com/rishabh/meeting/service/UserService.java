package com.rishabh.meeting.service;

import com.rishabh.meeting.dto.UpdateProfileRequest;
import com.rishabh.meeting.entity.User;
import com.rishabh.meeting.exception.ResourceNotFoundException;
import com.rishabh.meeting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        user.setName(request.getName());
        if (request.getProfilePicture() != null) {
            user.setProfilePicture(request.getProfilePicture());
        }
        userRepository.save(user);
    }

    @Transactional
    public void deleteProfile(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Incorrect password");
        }
        
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public java.util.List<com.rishabh.meeting.dto.UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(com.rishabh.meeting.dto.UserDto::fromEntity)
                .toList();
    }
}
