package com.foodbridge.controller;

import com.foodbridge.dto.auth.*;
import com.foodbridge.entity.User;
import com.foodbridge.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService auth;

    @PostMapping("/register/consumer")
    public User consumer(@Valid @RequestBody ConsumerRegisterRequest r) { return auth.registerConsumer(r); }

    @PostMapping("/register/supershop")
    public User supershop(@Valid @RequestBody PartnerRegisterRequest r) { return auth.registerSupershop(r); }

    @PostMapping("/register/donor")
    public User donor(@Valid @RequestBody PartnerRegisterRequest r) { return auth.registerDonor(r); }

    @PostMapping("/register/charity")
    public User charity(@Valid @RequestBody CharityRegisterRequest r) { return auth.registerCharity(r); }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest r) { return auth.login(r); }

    @GetMapping("/health")
    public Map<String,String> health() { return Map.of("status", "FoodBridge backend is running"); }
}
