package com.rishabh.meeting.controller;

import com.rishabh.meeting.dto.RegisterRequest;
import com.rishabh.meeting.dto.UserDto;
import com.rishabh.meeting.service.AuthService;
import com.rishabh.meeting.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createAdmin(
            @Valid @RequestBody RegisterRequest request) {

        Map<String, String> response = authService.createAdmin(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
