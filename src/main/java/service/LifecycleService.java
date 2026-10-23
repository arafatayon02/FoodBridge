package com.foodrescue.retail.service;

import com.foodrescue.retail.dto.LifecycleResult;
import com.foodrescue.retail.model.Listing;
import com.foodrescue.retail.model.ListingStatus;
import com.foodrescue.retail.model.NotificationType;
import com.foodrescue.retail.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LifecycleService {

    private final ListingRepository repo;
    private final NotificationService notifications;

    @Value("${app.lifecycle.near-cutoff-minutes:30}")
    private int nearCutoffMinutes;

    @Value("${app.lifecycle.purge-retention-days:7}")
    private int purgeRetentionDays;

    @Transactional
    public LifecycleResult runAll() {
        int expired = expireListings();
        int near = notifyNearCutoff();
        int purged = purgeOldListings();
        if (expired + near + purged > 0) {
            log.info("Lifecycle run: expired={}, nearCutoff={}, purged={}", expired, near, purged);
        }
        return new LifecycleResult(expired, near, purged);
    }

    /** FOOD-31 / FOOD-32: expire active listings that passed their cutoff and notify the store. */
    @Transactional
    public int expireListings() {
        LocalDateTime now = LocalDateTime.now();
        List<Listing> due = repo.findByStatusAndCutoffAtBefore(ListingStatus.ACTIVE, now);
        for (Listing l : due) {
            l.setStatus(ListingStatus.EXPIRED);
            l.setExpiredAt(now);
            if (!l.isExpiryNotified()) {
                notifications.notify(l.getStoreId(), l.getId(), NotificationType.LISTING_EXPIRED,
                        "Your listing \"" + l.getTitle() + "\" has expired and is no longer visible to customers.");
                l.setExpiryNotified(true);
            }
        }
        repo.saveAll(due);
        return due.size();
    }

    /** FOOD-34: warn the store N minutes before the cutoff. */
    @Transactional
    public int notifyNearCutoff() {
        LocalDateTime now = LocalDateTime.now();
        List<Listing> near = repo.findByStatusAndNearCutoffNotifiedFalseAndCutoffAtBetween(
                ListingStatus.ACTIVE, now, now.plusMinutes(nearCutoffMinutes));
        for (Listing l : near) {
            long mins = java.time.Duration.between(now, l.getCutoffAt()).toMinutes();
            notifications.notify(l.getStoreId(), l.getId(), NotificationType.NEAR_CUTOFF,
                    "Listing \"" + l.getTitle() + "\" closes in about " + mins + " minute(s). "
                            + "Consider lowering the price or adjusting the pickup window.");
            l.setNearCutoffNotified(true);
        }
        repo.saveAll(near);
        return near.size();
    }

    /** FOOD-33: hard-delete expired listings older than the retention period. */
    @Transactional
    public int purgeOldListings() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(purgeRetentionDays);
        return repo.purgeOlderThan(ListingStatus.EXPIRED, threshold);
    }
}
