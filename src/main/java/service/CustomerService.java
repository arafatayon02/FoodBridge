package com.foodbridge.backend.service;

import com.foodbridge.backend.dto.RegisterCustomerRequest;
import com.foodbridge.backend.entity.Role;
import com.foodbridge.backend.entity.User;
import com.foodbridge.backend.exception.ApiException;
import com.foodbridge.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterCustomerRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("An account with this email already exists", HttpStatus.CONFLICT);
        }

        User customer = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CONSUMER)
                .isActive(true)
                .build();

        return userRepository.save(customer);
    }
}
