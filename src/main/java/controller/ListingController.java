package com.foodrescue.retail.controller;

import com.foodrescue.retail.dto.*;
import com.foodrescue.retail.model.Listing;
import com.foodrescue.retail.service.ListingMapper;
import com.foodrescue.retail.service.ListingService;
import com.foodrescue.retail.service.SafetyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService service;
    private final SafetyService safety;
    private final ListingMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ListingSummary create(@Valid @RequestBody ListingRequest req) {
        return mapper.toSummary(service.create(req));
    }

    @GetMapping("/{id}")
    public ListingSummary get(@PathVariable Long id) {
        return mapper.toSummary(service.get(id));
    }

    @GetMapping("/store/{storeId}")
    public List<ListingSummary> byStore(@PathVariable Long storeId) {
        return service.byStore(storeId).stream().map(mapper::toSummary).toList();
    }

    @PutMapping("/{id}")
    public ListingSummary update(@PathVariable Long id, @Valid @RequestBody ListingRequest req) {
        return mapper.toSummary(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ListingSummary cancel(@PathVariable Long id) {
        return mapper.toSummary(service.cancel(id));
    }

    /** FOOD-30 */
    @PostMapping("/{id}/duplicate")
    @ResponseStatus(HttpStatus.CREATED)
    public ListingSummary duplicate(@PathVariable Long id, @RequestBody(required = false) DuplicateRequest req) {
        return mapper.toSummary(service.duplicate(id, req));
    }

    /** FOOD-32 */
    @GetMapping("/{id}/safety-countdown")
    public SafetyCountdown safetyCountdown(@PathVariable Long id) {
        Listing l = service.get(id);
        return safety.countdown(l);
    }
}
