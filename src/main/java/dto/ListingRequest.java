package com.foodbridge.supershop.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
public record ListingRequest(@NotNull @Positive Long supershopId,@NotBlank @Size(max=180) String productName,
                             @NotBlank @Size(max=80) String sku,@NotNull @DecimalMin("0.01") BigDecimal originalPrice,
                             @NotNull @DecimalMin("0.00") @DecimalMax("90.00") BigDecimal discountPercent,
                             @Min(1) @Max(100000) int quantity,@NotNull @FutureOrPresent LocalDate expiryDate) {}
