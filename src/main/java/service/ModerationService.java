package com.foodrescue.consumer.service;

import com.foodrescue.consumer.exception.NotFoundException;
import com.foodrescue.consumer.model.ListingEntry;
import com.foodrescue.consumer.model.ListingEntry.ModerationStatus;
import com.foodrescue.consumer.repository.ListingEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/** FOOD-82: Build Listing Moderation Module. */
@Service
@RequiredArgsConstructor
public class ModerationService {

    private final ListingEntryRepository repo;
    private final ConfigService config;

    @Transactional
    public ListingEntry submit(ListingEntry l) {
        l.setId(null);
        l.setStatus(ModerationStatus.PENDING);
        return repo.save(l);
    }

    @Transactional(readOnly = true)
    public Page<ListingEntry> queue(ModerationStatus status, int page, int size) {
        PageRequest pr = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "reportCount").and(Sort.by(Sort.Direction.ASC, "createdAt")));
        return repo.findByStatus(status == null ? ModerationStatus.PENDING : status, pr);
    }

    @Transactional
    public ListingEntry approve(Long id, String moderator) {
        return decide(id, ModerationStatus.APPROVED, moderator, null);
    }

    @Transactional
    public ListingEntry reject(Long id, String moderator, String reason) {
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("A rejection reason is required");
        return decide(id, ModerationStatus.REJECTED, moderator, reason);
    }

    @Transactional
    public ListingEntry flag(Long id, String moderator, String reason) {
        return decide(id, ModerationStatus.FLAGGED, moderator, reason);
    }

    /** Consumers can report a listing; it is auto-flagged once the configured threshold is reached. */
    @Transactional
    public ListingEntry report(Long id) {
        ListingEntry l = get(id);
        l.setReportCount(l.getReportCount() + 1);
        if (l.getReportCount() >= config.getInt(ConfigService.AUTO_FLAG_THRESHOLD)
                && l.getStatus() == ModerationStatus.APPROVED) {
            l.setStatus(ModerationStatus.FLAGGED);
            l.setModerationReason("Auto-flagged after " + l.getReportCount() + " consumer reports");
            l.setModeratedBy("system");
            l.setModeratedAt(LocalDateTime.now());
        }
        return repo.save(l);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> stats() {
        Map<String, Long> m = new LinkedHashMap<>();
        for (ModerationStatus s : ModerationStatus.values()) m.put(s.name(), repo.countByStatus(s));
        return m;
    }

    @Transactional(readOnly = true)
    public ListingEntry get(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Listing " + id + " not found"));
    }

    private ListingEntry decide(Long id, ModerationStatus status, String moderator, String reason) {
        ListingEntry l = get(id);
        l.setStatus(status);
        l.setModerationReason(reason);
        l.setModeratedBy(moderator);
        l.setModeratedAt(LocalDateTime.now());
        return repo.save(l);
    }
}
