package com.foodbridge.backend.controller;

import com.foodbridge.backend.dto.VerifyDonorRequest;
import com.foodbridge.backend.dto.VerifySupershopRequest;
import com.foodbridge.backend.entity.RestaurantDonor;
import com.foodbridge.backend.entity.Supershop;
import com.foodbridge.backend.service.DonorService;
import com.foodbridge.backend.service.SupershopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SupershopService supershopService;
    private final DonorService donorService;

    // ---------- FOOD-19: Admin Supershop Verification (Retail Track) ----------

    @GetMapping("/supershops/pending")
    public ResponseEntity<List<Supershop>> listPendingSupershops() {
        return ResponseEntity.ok(supershopService.listPending());
    }

    @PutMapping("/supershops/{supershopId}/verify")
    public ResponseEntity<Supershop> verifySupershop(@PathVariable Long supershopId,
                                                     @Valid @RequestBody VerifySupershopRequest request,
                                                     Authentication authentication) {
        Supershop result = supershopService.verifySupershop(supershopId, request, authentication.getName());
        return ResponseEntity.ok(result);
    }

    // ---------- FOOD-60: Donor Approval Status (Donation Track) ----------

    @GetMapping("/donors/pending")
    public ResponseEntity<List<RestaurantDonor>> listPendingDonors() {
        return ResponseEntity.ok(donorService.listPending());
    }

    @PutMapping("/donors/{donorId}/verify")
    public ResponseEntity<RestaurantDonor> verifyDonor(@PathVariable Long donorId,
                                                       @Valid @RequestBody VerifyDonorRequest request,
                                                       Authentication authentication) {
        RestaurantDonor result = donorService.verifyDonor(donorId, request, authentication.getName());
        return ResponseEntity.ok(result);
    }
}
