package com.foodbridge.backend.dto;

import com.foodbridge.backend.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String email;
    private String name;
    private Role role;
    private Long supershopId; // null for non-supershop-staff roles
}
