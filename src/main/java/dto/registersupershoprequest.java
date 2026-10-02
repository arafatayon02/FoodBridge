package com.foodbridge.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterSupershopRequest {

    @NotBlank(message = "Business name is required")
    private String businessName;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Contact person name is required")
    private String contactPerson;

    @NotBlank(message = "Contact phone is required")
    private String contactPhone;

    // Credentials for the first (owner) staff login created alongside the branch
    @NotBlank(message = "Owner name is required")
    private String ownerName;

    @Email(message = "A valid email is required")
    @NotBlank
    private String ownerEmail;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String ownerPassword;
}
