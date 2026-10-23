package com.foodrescue.consumer.controller;

import com.foodrescue.consumer.dto.ModerationRequest;
import com.foodrescue.consumer.model.ListingEntry;
import com.foodrescue.consumer.service.ModerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** FOOD-82 */
@RestController
@RequiredArgsConstructor
public class ModerationController {

    private final ModerationService moderation;

    @PostMapping("/api/moderation/listings")
    @ResponseStatus(HttpStatus.CREATED)
    public ListingEntry submit(@RequestBody ListingEntry l) {
        return moderation.submit(l);
    }

    @PostMapping("/api/moderation/listings/{id}/report")
    public ListingEntry report(@PathVariable Long id) {
        return moderation.report(id);
    }

    @GetMapping("/api/admin/moderation/listings")
    public Page<ListingEntry> queue(@RequestParam(required = false) ListingEntry.ModerationStatus status,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        return moderation.queue(status, page, size);
    }

    @GetMapping("/api/admin/moderation/stats")
    public Map<String, Long> stats() {
        return moderation.stats();
    }

    @PostMapping("/api/admin/moderation/listings/{id}/approve")
    public ListingEntry approve(@PathVariable Long id, @Valid @RequestBody ModerationRequest r) {
        return moderation.approve(id, r.moderator());
    }

    @PostMapping("/api/admin/moderation/listings/{id}/reject")
    public ListingEntry reject(@PathVariable Long id, @Valid @RequestBody ModerationRequest r) {
        return moderation.reject(id, r.moderator(), r.reason());
    }

    @PostMapping("/api/admin/moderation/listings/{id}/flag")
    public ListingEntry flag(@PathVariable Long id, @Valid @RequestBody ModerationRequest r) {
        return moderation.flag(id, r.moderator(), r.reason());
    }
}
