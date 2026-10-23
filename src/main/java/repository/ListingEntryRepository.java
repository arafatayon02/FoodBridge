package com.foodrescue.consumer.repository;

import com.foodrescue.consumer.model.ListingEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingEntryRepository extends JpaRepository<ListingEntry, Long> {
    Page<ListingEntry> findByStatus(ListingEntry.ModerationStatus status, Pageable pageable);
    long countByStatus(ListingEntry.ModerationStatus status);
}
