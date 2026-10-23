package com.foodrescue.consumer.dto;

import java.time.LocalDate;
import java.util.List;

public record StoreSafetyInfo(
        Long storeId, String name, String verificationStatus, boolean verified,
        Integer hygieneRating, String hygieneCertNumber, String foodLicenseNumber,
        LocalDate lastInspectionDate, boolean inspectionCurrent, Double averageRating,
        String allergenPolicy, List<String> safetyChecklist, String pickupGuidelines,
        List<String> warnings) {
}
