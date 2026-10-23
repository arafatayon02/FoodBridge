package com.foodrescue.consumer.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ImpactReport(
        LocalDate from, LocalDate to,
        long totalOrders, long mealsRescued,
        BigDecimal foodSavedKg, BigDecimal co2AvoidedKg,
        BigDecimal moneySavedByConsumers, BigDecimal revenueRecovered,
        List<StoreImpact> byStore, List<MonthlyImpact> byMonth) {

    public record StoreImpact(Long storeId, String storeName, long orders, long meals, BigDecimal foodSavedKg) {}

    public record MonthlyImpact(String month, long orders, long meals, BigDecimal foodSavedKg,
                                BigDecimal co2AvoidedKg, BigDecimal moneySaved) {}
}
