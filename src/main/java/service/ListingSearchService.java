package com.foodrescue.retail.service;

import com.foodrescue.retail.dto.ListingSummary;
import com.foodrescue.retail.dto.SearchCriteria;
import com.foodrescue.retail.model.Listing;
import com.foodrescue.retail.model.ListingStatus;
import com.foodrescue.retail.repository.ListingRepository;
import com.foodrescue.retail.util.GeoUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListingSearchService {

    private final ListingRepository repo;
    private final ListingMapper mapper;

    /** FOOD-41: Listing Search and Filters. Only bookable listings are returned. */
    @Transactional(readOnly = true)
    public Page<ListingSummary> search(SearchCriteria c, int page, int size) {
        LocalDateTime now = LocalDateTime.now();

        Specification<Listing> spec = (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            ps.add(cb.equal(root.get("status"), ListingStatus.ACTIVE));
            ps.add(cb.greaterThan(root.get("cutoffAt"), now));
            ps.add(cb.greaterThan(root.get("quantity"), 0));

            if (StringUtils.hasText(c.q())) {
                String like = "%" + c.q().trim().toLowerCase() + "%";
                ps.add(cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("description")), like),
                        cb.like(cb.lower(root.get("category")), like)));
            }
            if (StringUtils.hasText(c.category())) {
                ps.add(cb.equal(cb.lower(root.get("category")), c.category().toLowerCase()));
            }
            if (StringUtils.hasText(c.dietaryTag())) {
                ps.add(cb.equal(cb.lower(root.get("dietaryTag")), c.dietaryTag().toLowerCase()));
            }
            if (c.minPrice() != null) ps.add(cb.greaterThanOrEqualTo(root.get("discountedPrice"), c.minPrice()));
            if (c.maxPrice() != null) ps.add(cb.lessThanOrEqualTo(root.get("discountedPrice"), c.maxPrice()));
            if (c.minDiscount() != null) ps.add(cb.greaterThanOrEqualTo(root.get("discountPercent"), c.minDiscount()));
            if (c.pickupBefore() != null) ps.add(cb.lessThanOrEqualTo(root.get("pickupStart"), c.pickupBefore()));

            if (c.lat() != null && c.lng() != null && c.radiusKm() != null) {
                double[] box = GeoUtils.boundingBox(c.lat(), c.lng(), c.radiusKm());
                ps.add(cb.between(root.get("latitude"), box[0], box[1]));
                ps.add(cb.between(root.get("longitude"), box[2], box[3]));
            }
            return cb.and(ps.toArray(new Predicate[0]));
        };

        PageRequest pr = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), sort(c.sort()));
        return repo.findAll(spec, pr).map(mapper::toSummary);
    }

    /** FOOD-39: Browse & Search Discounted Listings (biggest discounts first by default). */
    @Transactional(readOnly = true)
    public Page<ListingSummary> browseDiscounted(String q, String category, Integer minDiscount, String sort,
                                                 int page, int size) {
        SearchCriteria c = new SearchCriteria(q, category, null, null, null,
                minDiscount != null ? Math.max(minDiscount, 1) : 1,
                null, null, null, null, sort == null ? "discount" : sort);
        return search(c, page, size);
    }

    private Sort sort(String key) {
        if (key == null) return Sort.by(Sort.Direction.DESC, "createdAt");
        return switch (key.toLowerCase()) {
            case "discount" -> Sort.by(Sort.Direction.DESC, "discountPercent");
            case "price" -> Sort.by(Sort.Direction.ASC, "discountedPrice");
            case "ending" -> Sort.by(Sort.Direction.ASC, "cutoffAt");
            case "pickup" -> Sort.by(Sort.Direction.ASC, "pickupStart");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }
}
