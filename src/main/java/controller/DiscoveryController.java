package com.foodrescue.retail.controller;

import com.foodrescue.retail.dto.ListingSummary;
import com.foodrescue.retail.dto.MapMarker;
import com.foodrescue.retail.dto.SearchCriteria;
import com.foodrescue.retail.service.ListingSearchService;
import com.foodrescue.retail.service.MapService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DiscoveryController {

    private final ListingSearchService searchService;
    private final MapService mapService;

    /** FOOD-41: GET /api/search/listings?q=bread&category=BAKERY&maxPrice=5&minDiscount=30&sort=discount */
    @GetMapping("/api/search/listings")
    public Page<ListingSummary> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String dietaryTag,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minDiscount,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime pickupBefore,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Double radiusKm,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        SearchCriteria c = new SearchCriteria(q, category, dietaryTag, minPrice, maxPrice, minDiscount,
                pickupBefore, lat, lng, radiusKm, sort);
        return searchService.search(c, page, size);
    }

    /** FOOD-39 */
    @GetMapping("/api/discover/discounted")
    public Page<ListingSummary> discounted(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer minDiscount,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return searchService.browseDiscounted(q, category, minDiscount, sort, page, size);
    }

    /** FOOD-42: markers around a point */
    @GetMapping("/api/map/listings")
    public List<MapMarker> nearby(@RequestParam double lat, @RequestParam double lng,
                                  @RequestParam(defaultValue = "5") double radiusKm,
                                  @RequestParam(required = false) String category) {
        return mapService.nearby(lat, lng, radiusKm, category);
    }

    /** FOOD-42: markers inside the visible map viewport */
    @GetMapping("/api/map/listings/bounds")
    public List<MapMarker> bounds(@RequestParam double minLat, @RequestParam double maxLat,
                                  @RequestParam double minLng, @RequestParam double maxLng) {
        return mapService.inBounds(minLat, maxLat, minLng, maxLng);
    }
}
