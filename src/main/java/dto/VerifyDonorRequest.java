package com.foodbridge.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VerifyDonorRequest {

    @NotNull(message = "approve must be true or false")
    private Boolean approve;

    // required when approve = false
    private String rejectionReason;
}
