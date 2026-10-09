package com.foodbridge.backend.controller;

import com.foodbridge.backend.dto.RegisterCustomerRequest;
import com.foodbridge.backend.entity.User;
import com.foodbridge.backend.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    // Feature 8 (backend): creates a CONSUMER-role account
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterCustomerRequest request) {
        User customer = customerService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }
}
