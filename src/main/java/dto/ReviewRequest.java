package com.foodbridge.supershop.dto;
import jakarta.validation.constraints.*;
public record ReviewRequest(@NotNull Boolean approve,@Size(max=500) String rejectionReason) {}
