package com.rishabh.meeting.controller;

import com.rishabh.meeting.dto.UpdateProfileRequest;
import com.rishabh.meeting.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/me")
    public ResponseEntity<Map<String, String>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        userService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Map<String, String>> deleteProfile(
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        userService.deleteProfile(userDetails.getUsername(), request.get("password"));
        return ResponseEntity.ok(Map.of("message", "Profile deleted successfully"));
    }
}
