package com.foodrescue.retail.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DuplicateRequest(
        LocalDateTime pickupStart,
        LocalDateTime pickupEnd,
        LocalDateTime expiresAt,
        Integer quantity,
        BigDecimal discountedPrice) {
}
