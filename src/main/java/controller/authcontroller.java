package com.foodbridge.backend.controller;

import com.foodbridge.backend.dto.ForgotPasswordRequest;
import com.foodbridge.backend.dto.LoginRequest;
import com.foodbridge.backend.dto.LoginResponse;
import com.foodbridge.backend.dto.ResetPasswordRequest;
import com.foodbridge.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Feature 6: single login endpoint for every role (supershop staff, donor
    // staff, charity, consumer, admin) - the JWT's "role" claim drives access.
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // Feature 2 (step 1): request an OTP be emailed to the account
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(Map.of("message", "If that email exists, an OTP has been sent."));
    }

    // Feature 2 (step 2): submit the OTP + new password
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(Map.of("message", "Password reset successful."));
    }
}
