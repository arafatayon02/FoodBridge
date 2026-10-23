package com.foodrescue.consumer.dto;

import com.foodrescue.consumer.model.Store;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record VerificationRequest(
        @NotNull Store.VerificationStatus status,
        Integer hygieneRating,
        String hygieneCertNumber,
        String foodLicenseNumber,
        LocalDate lastInspectionDate) {
}
