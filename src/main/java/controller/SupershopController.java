package com.foodbridge.backend.controller;

import com.foodbridge.backend.dto.AddStaffRequest;
import com.foodbridge.backend.dto.RegisterSupershopRequest;
import com.foodbridge.backend.entity.Supershop;
import com.foodbridge.backend.entity.User;
import com.foodbridge.backend.service.SupershopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supershops")
@RequiredArgsConstructor
public class SupershopController {

    private final SupershopService supershopService;

    // Feature 1 + 4: public registration -> creates Supershop with status = PENDING
    @PostMapping("/register")
    public ResponseEntity<Supershop> register(@Valid @RequestBody RegisterSupershopRequest request) {
        Supershop supershop = supershopService.registerSupershop(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(supershop);
    }

    // Feature 5: add another staff login under the same branch.
    // Requester must already be authenticated staff of this supershop (enforced in service).
    @PostMapping("/{supershopId}/staff")
    public ResponseEntity<User> addStaff(@PathVariable Long supershopId,
                                         @Valid @RequestBody AddStaffRequest request,
                                         Authentication authentication) {
        User staff = supershopService.addStaff(supershopId, request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(staff);
    }

    @GetMapping("/{supershopId}/staff")
    public ResponseEntity<List<User>> listStaff(@PathVariable Long supershopId) {
        return ResponseEntity.ok(supershopService.listStaff(supershopId));
    }

    @GetMapping("/{supershopId}")
    public ResponseEntity<Supershop> getSupershop(@PathVariable Long supershopId) {
        return ResponseEntity.ok(supershopService.getSupershopOrThrow(supershopId));
    }
}
