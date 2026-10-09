package com.foodbridge.backend.controller;

import com.foodbridge.backend.dto.LoginRequest;
import com.foodbridge.backend.dto.LoginResponse;
import com.foodbridge.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * FOOD-79: Build Secure Admin Login.
 * Kept separate from the generic /api/auth/login so the admin portal's
 * login form always hits a route that rejects non-admin accounts outright.
 */
@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> adminLogin(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.adminLogin(request));
    }
}
