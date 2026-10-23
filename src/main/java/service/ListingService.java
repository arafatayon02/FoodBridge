package com.foodrescue.retail.service;

import com.foodrescue.retail.dto.DuplicateRequest;
import com.foodrescue.retail.dto.ListingRequest;
import com.foodrescue.retail.exception.NotFoundException;
import com.foodrescue.retail.model.Listing;
import com.foodrescue.retail.model.ListingStatus;
import com.foodrescue.retail.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListingService {

    private final ListingRepository repo;
    private final SafetyService safety;

    @Transactional
    public Listing create(ListingRequest r) {
        Listing l = new Listing();
        apply(l, r);
        return repo.save(l);
    }

    @Transactional
    public Listing update(Long id, ListingRequest r) {
        Listing l = get(id);
        if (l.getStatus() != ListingStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE listings can be edited");
        }
        apply(l, r);
        // times may have changed -> allow notifications to fire again
        l.setNearCutoffNotified(false);
        l.setExpiryNotified(false);
        return repo.save(l);
    }

    @Transactional
    public Listing cancel(Long id) {
        Listing l = get(id);
        l.setStatus(ListingStatus.CANCELLED);
        return repo.save(l);
    }

    @Transactional(readOnly = true)
    public Listing get(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Listing " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<Listing> byStore(Long storeId) {
        return repo.findByStoreIdOrderByCreatedAtDesc(storeId);
    }

    /** FOOD-30: Implement Listing Duplication. Copies a listing into a fresh ACTIVE one. */
    @Transactional
    public Listing duplicate(Long sourceId, DuplicateRequest req) {
        Listing src = get(sourceId);
        LocalDateTime now = LocalDateTime.now();
        DuplicateRequest d = req == null ? new DuplicateRequest(null, null, null, null, null) : req;

        // default: shift the original window so pickup starts 1h from now
        Duration shift = Duration.between(src.getPickupStart(), now.plusHours(1));

        Listing copy = new Listing();
        copy.setStoreId(src.getStoreId());
        copy.setTitle(src.getTitle());
        copy.setDescription(src.getDescription());
        copy.setCategory(src.getCategory());
        copy.setDietaryTag(src.getDietaryTag());
        copy.setOriginalPrice(src.getOriginalPrice());
        copy.setDiscountedPrice(d.discountedPrice() != null ? d.discountedPrice() : src.getDiscountedPrice());
        copy.setQuantity(d.quantity() != null ? d.quantity() : src.getQuantity());
        copy.setLatitude(src.getLatitude());
        copy.setLongitude(src.getLongitude());
        copy.setPreparedAt(now);
        copy.setBestBefore(src.getBestBefore() != null ? src.getBestBefore().plus(shift) : null);
        copy.setPickupStart(d.pickupStart() != null ? d.pickupStart() : src.getPickupStart().plus(shift));
        copy.setPickupEnd(d.pickupEnd() != null ? d.pickupEnd() : src.getPickupEnd().plus(shift));
        copy.setExpiresAt(d.expiresAt() != null ? d.expiresAt() : src.getExpiresAt().plus(shift));
        copy.setDuplicatedFromId(src.getId());

        validate(copy);
        copy.setDiscountPercent(discountPercent(copy.getOriginalPrice(), copy.getDiscountedPrice()));
        copy.setCutoffAt(safety.computeCutoff(copy));
        return repo.save(copy);
    }

    private void apply(Listing l, ListingRequest r) {
        l.setStoreId(r.storeId());
        l.setTitle(r.title());
        l.setDescription(r.description());
        l.setCategory(r.category());
        l.setDietaryTag(r.dietaryTag());
        l.setOriginalPrice(r.originalPrice());
        l.setDiscountedPrice(r.discountedPrice());
        l.setQuantity(r.quantity());
        l.setLatitude(r.latitude());
        l.setLongitude(r.longitude());
        l.setPreparedAt(r.preparedAt());
        l.setBestBefore(r.bestBefore());
        l.setPickupStart(r.pickupStart());
        l.setPickupEnd(r.pickupEnd());
        l.setExpiresAt(r.expiresAt());
        validate(l);
        l.setDiscountPercent(discountPercent(r.originalPrice(), r.discountedPrice()));
        l.setCutoffAt(safety.computeCutoff(l));
    }

    private void validate(Listing l) {
        if (l.getDiscountedPrice().compareTo(l.getOriginalPrice()) > 0) {
            throw new IllegalArgumentException("discountedPrice cannot exceed originalPrice");
        }
        if (!l.getPickupEnd().isAfter(l.getPickupStart())) {
            throw new IllegalArgumentException("pickupEnd must be after pickupStart");
        }
        if (!l.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("expiresAt must be in the future");
        }
    }

    static int discountPercent(BigDecimal original, BigDecimal discounted) {
        if (original.signum() == 0) return 0;
        return original.subtract(discounted).multiply(BigDecimal.valueOf(100))
                .divide(original, 0, RoundingMode.HALF_UP).intValue();
    }
}
