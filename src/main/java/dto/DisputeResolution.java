package com.foodrescue.consumer.dto;

import com.foodrescue.consumer.model.Dispute;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DisputeResolution(
        @NotNull Dispute.Decision decision,
        BigDecimal refundAmount,
        String note,
        @NotBlank String resolvedBy) {
}
