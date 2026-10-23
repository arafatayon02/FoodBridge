package com.foodrescue.retail.service;

import com.foodrescue.retail.dto.SafetyCountdown;
import com.foodrescue.retail.model.Listing;
import com.foodrescue.retail.model.SafetyLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class SafetyService {

    private final int defaultWindowHours;
    private final int hotFoodWindowHours;
    private final int warningMinutes;
    private final int criticalMinutes;

    public SafetyService(@Value("${app.safety.default-window-hours:6}") int defaultWindowHours,
                         @Value("${app.safety.hot-food-window-hours:3}") int hotFoodWindowHours,
                         @Value("${app.safety.warning-minutes:120}") int warningMinutes,
                         @Value("${app.safety.critical-minutes:30}") int criticalMinutes) {
        this.defaultWindowHours = defaultWindowHours;
        this.hotFoodWindowHours = hotFoodWindowHours;
        this.warningMinutes = warningMinutes;
        this.criticalMinutes = criticalMinutes;
    }

    public LocalDateTime computeCutoff(Listing l) {
        LocalDateTime cutoff = l.getExpiresAt();
        if (l.getBestBefore() != null && l.getBestBefore().isBefore(cutoff)) {
            cutoff = l.getBestBefore();
        }
        if (l.getPreparedAt() != null) {
            int hours = "HOT_MEAL".equalsIgnoreCase(l.getCategory()) ? hotFoodWindowHours : defaultWindowHours;
            LocalDateTime safeUntil = l.getPreparedAt().plusHours(hours);
            if (safeUntil.isBefore(cutoff)) cutoff = safeUntil;
        }
        return cutoff;
    }

    public long secondsRemaining(Listing l) {
        return Duration.between(LocalDateTime.now(), l.getCutoffAt()).getSeconds();
    }

    public SafetyLevel level(long secondsRemaining) {
        if (secondsRemaining <= 0) return SafetyLevel.UNSAFE;
        if (secondsRemaining <= criticalMinutes * 60L) return SafetyLevel.CRITICAL;
        if (secondsRemaining <= warningMinutes * 60L) return SafetyLevel.WARNING;
        return SafetyLevel.SAFE;
    }

    public SafetyCountdown countdown(Listing l) {
        long secs = secondsRemaining(l);
        SafetyLevel level = level(secs);
        return new SafetyCountdown(l.getId(), l.getCutoffAt(), Math.max(secs, 0), format(secs), level,
                level != SafetyLevel.UNSAFE && l.getQuantity() != null && l.getQuantity() > 0);
    }

    private String format(long secs) {
        if (secs <= 0) return "00:00:00";
        return String.format("%02d:%02d:%02d", secs / 3600, (secs % 3600) / 60, secs % 60);
    }
}
