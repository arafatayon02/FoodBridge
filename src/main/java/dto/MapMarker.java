package com.foodrescue.retail.dto;

import com.foodrescue.retail.model.SafetyLevel;

import java.math.BigDecimal;

public record MapMarker(
        Long id, String title, String category,
        double latitude, double longitude,
        BigDecimal discountedPrice, Integer discountPercent,
        long secondsRemaining, SafetyLevel safetyLevel,
        Double distanceKm) {
}
