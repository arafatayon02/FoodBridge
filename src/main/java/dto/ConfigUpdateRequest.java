package com.foodrescue.consumer.dto;

import jakarta.validation.constraints.NotBlank;

public record ConfigUpdateRequest(@NotBlank String value, @NotBlank String updatedBy) {
}
