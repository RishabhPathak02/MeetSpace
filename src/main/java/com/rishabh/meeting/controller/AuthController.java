package com.rishabh.meeting.controller;

import com.rishabh.meeting.dto.LoginRequest;
import com.rishabh.meeting.dto.LoginResponse;
import com.rishabh.meeting.dto.RegisterRequest;
import com.rishabh.meeting.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Public authentication endpoints — no JWT required.
 *
 *  POST /api/auth/register  → register a new USER
 *  POST /api/auth/login     → authenticate and receive a JWT
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @Valid @RequestBody RegisterRequest request) {

        Map<String, String> response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
