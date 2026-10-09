package com.foodbridge.backend.controller;

import com.foodbridge.backend.dto.AddStaffRequest;
import com.foodbridge.backend.dto.RegisterDonorRequest;
import com.foodbridge.backend.entity.RestaurantDonor;
import com.foodbridge.backend.entity.User;
import com.foodbridge.backend.service.DonorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * FOOD-58 / FOOD-59: Restaurant/Bakery registration & staff management.
 */
@RestController
@RequestMapping("/api/donors")
@RequiredArgsConstructor
public class DonorController {

    private final DonorService donorService;

    // FOOD-59: public registration -> creates RestaurantDonor with status = PENDING
    @PostMapping("/register")
    public ResponseEntity<RestaurantDonor> register(@Valid @RequestBody RegisterDonorRequest request) {
        RestaurantDonor donor = donorService.registerDonor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(donor);
    }

    // FOOD-58: add another staff login under the same business.
    // Requester must already be authenticated staff of this business (enforced in service).
    @PostMapping("/{donorId}/staff")
    public ResponseEntity<User> addStaff(@PathVariable Long donorId,
                                         @Valid @RequestBody AddStaffRequest request,
                                         Authentication authentication) {
        User staff = donorService.addStaff(donorId, request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(staff);
    }

    @GetMapping("/{donorId}/staff")
    public ResponseEntity<List<User>> listStaff(@PathVariable Long donorId) {
        return ResponseEntity.ok(donorService.listStaff(donorId));
    }

    @GetMapping("/{donorId}")
    public ResponseEntity<RestaurantDonor> getDonor(@PathVariable Long donorId) {
        return ResponseEntity.ok(donorService.getDonorOrThrow(donorId));
    }
}
