package com.foodrescue.consumer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OrderRequest(
        @NotNull Long consumerId,
        @NotNull Long storeId,
        @NotBlank String listingTitle,
        @NotNull @Min(1) Integer quantity,
        @NotNull @DecimalMin("0.0") BigDecimal originalTotal,
        @NotNull @DecimalMin("0.0") BigDecimal paidTotal) {
}
