package com.foodrescue.retail.service;

import com.foodrescue.retail.dto.ListingSummary;
import com.foodrescue.retail.model.Listing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListingMapper {

    private final SafetyService safety;

    public ListingSummary toSummary(Listing l) {
        long secs = safety.secondsRemaining(l);
        return new ListingSummary(l.getId(), l.getStoreId(), l.getTitle(), l.getCategory(), l.getDietaryTag(),
                l.getOriginalPrice(), l.getDiscountedPrice(), l.getDiscountPercent(), l.getQuantity(),
                l.getLatitude(), l.getLongitude(), l.getPickupStart(), l.getPickupEnd(), l.getCutoffAt(),
                Math.max(secs, 0), safety.level(secs));
    }
}
