package com.foodrescue.retail.dto;

import com.foodrescue.retail.model.SafetyLevel;

import java.time.LocalDateTime;

public record SafetyCountdown(
        Long listingId,
        LocalDateTime cutoffAt,
        long secondsRemaining,
        String formattedRemaining,
        SafetyLevel level,
        boolean purchasable) {
}
