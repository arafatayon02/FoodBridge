package com.foodrescue.retail.controller;

import com.foodrescue.retail.dto.LifecycleResult;
import com.foodrescue.retail.model.Notification;
import com.foodrescue.retail.service.LifecycleService;
import com.foodrescue.retail.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notifications;
    private final LifecycleService lifecycle;

    /** FOOD-31 / FOOD-34: inbox of expiry and near-cutoff notifications */
    @GetMapping("/api/stores/{storeId}/notifications")
    public List<Notification> list(@PathVariable Long storeId,
                                   @RequestParam(defaultValue = "false") boolean unreadOnly) {
        return notifications.forStore(storeId, unreadOnly);
    }

    @PostMapping("/api/notifications/{id}/seen")
    public Notification seen(@PathVariable Long id) {
        return notifications.markSeen(id);
    }

    /** Manually trigger the expiry / near-cutoff / purge cycle (FOOD-31, 33, 34) - handy for testing. */
    @PostMapping("/api/admin/lifecycle/run")
    public LifecycleResult runLifecycle() {
        return lifecycle.runAll();
    }
}
