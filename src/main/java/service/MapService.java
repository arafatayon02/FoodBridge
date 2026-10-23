package com.foodrescue.retail.service;

import com.foodrescue.retail.dto.MapMarker;
import com.foodrescue.retail.model.Listing;
import com.foodrescue.retail.repository.ListingRepository;
import com.foodrescue.retail.util.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/** FOOD-42: Live Map Listing View - lightweight markers, always reflecting live availability. */
@Service
@RequiredArgsConstructor
public class MapService {

    private final ListingRepository repo;
    private final SafetyService safety;

    @Transactional(readOnly = true)
    public List<MapMarker> nearby(double lat, double lng, double radiusKm, String category) {
        double[] b = GeoUtils.boundingBox(lat, lng, radiusKm);
        return repo.findInBounds(LocalDateTime.now(), b[0], b[1], b[2], b[3]).stream()
                .filter(l -> category == null || category.isBlank() || category.equalsIgnoreCase(l.getCategory()))
                .map(l -> marker(l, GeoUtils.distanceKm(lat, lng, l.getLatitude(), l.getLongitude())))
                .filter(m -> m.distanceKm() <= radiusKm)
                .sorted(Comparator.comparingDouble(MapMarker::distanceKm))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MapMarker> inBounds(double minLat, double maxLat, double minLng, double maxLng) {
        if (minLat > maxLat || minLng > maxLng) throw new IllegalArgumentException("Invalid bounds");
        return repo.findInBounds(LocalDateTime.now(), minLat, maxLat, minLng, maxLng).stream()
                .map(l -> marker(l, null)).toList();
    }

    private MapMarker marker(Listing l, Double distance) {
        long secs = safety.secondsRemaining(l);
        return new MapMarker(l.getId(), l.getTitle(), l.getCategory(), l.getLatitude(), l.getLongitude(),
                l.getDiscountedPrice(), l.getDiscountPercent(), Math.max(secs, 0), safety.level(secs),
                distance == null ? null : Math.round(distance * 100.0) / 100.0);
    }
}
