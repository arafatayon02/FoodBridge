package com.foodbridge.backend.controller;
import com.foodbridge.backend.entity.*;import com.foodbridge.backend.repository.*;
import com.foodbridge.backend.service.DonorService;
import lombok.RequiredArgsConstructor;import jakarta.validation.Valid;
import jakarta.validation.constraints.*;import org.springframework.http.*;import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;import java.time.*;import java.util.*;
@RestController @RequiredArgsConstructor @RequestMapping("/api/donation-listings")
public class DonationListingController {
    private final DonationListingRepository listings;private final UserRepository users;private final DonorService donors;
    public record Create(@NotBlank String title,@NotBlank String description,@Min(1) int portions,
                         @NotBlank String pickupAddress,@NotNull LocalDateTime pickupBefore){}
    private void authorize(Long id,Authentication a){
        if(a==null)throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED);
        User u=users.findByEmail(a.getName()).orElseThrow();
        if(u.getRole()!=Role.DONOR_STAFF||u.getRestaurantDonor()==null||!id.equals(u.getRestaurantDonor().getId()))
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN);
        if(donors.getDonorOrThrow(id).getStatus()!=DonorStatus.APPROVED)
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN,"Donor not approved");
    }
    @PostMapping("/donor/{id}") @ResponseStatus(HttpStatus.CREATED)
    public DonationListing create(@PathVariable Long id,@Valid @RequestBody Create r,Authentication a){
        authorize(id,a);if(!r.pickupBefore().isAfter(LocalDateTime.now()))throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST,"Pickup must be in future");
        DonationListing l=new DonationListing();l.setDonor(donors.getDonorOrThrow(id));l.setTitle(r.title());l.setDescription(r.description());l.setPortions(r.portions());l.setPickupAddress(r.pickupAddress());l.setPickupBefore(r.pickupBefore());return listings.save(l);
    }
    @GetMapping("/available") public List<DonationListing> available(){return listings.findByStatus(DonationListing.Status.AVAILABLE).stream().filter(l->l.getPickupBefore().isAfter(LocalDateTime.now())).toList();}
    @GetMapping("/donor/{id}") public List<DonationListing> mine(@PathVariable Long id,Authentication a){authorize(id,a);return listings.findByDonorId(id);}
    @PatchMapping("/{id}/cancel") public DonationListing cancel(@PathVariable Long id,Authentication a){
        DonationListing l=listings.findById(id).orElseThrow();authorize(l.getDonor().getId(),a);l.setStatus(DonationListing.Status.CANCELLED);return listings.save(l);
    }
}
