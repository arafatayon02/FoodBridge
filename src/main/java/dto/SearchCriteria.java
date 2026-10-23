package com.foodrescue.retail.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SearchCriteria(
        String q, String category, String dietaryTag,
        BigDecimal minPrice, BigDecimal maxPrice, Integer minDiscount,
        LocalDateTime pickupBefore,
        Double lat, Double lng, Double radiusKm,
        String sort) {
}
