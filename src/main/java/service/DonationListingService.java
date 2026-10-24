package com.foodbridge.donation;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DonationListingService {
    private final DonationListingRepository repository;
    public DonationListingService(DonationListingRepository repository){this.repository=repository;}
    @Transactional(readOnly=true) public List<DonationListing> available(){return repository.findCurrentlyAvailable(LocalDateTime.now());}
    @Transactional(readOnly=true) public List<DonationListing> all(){return repository.findAll();}
    @Transactional(readOnly=true) public DonationListing get(long id){return repository.findById(id).orElseThrow(()->new EntityNotFoundException("Donation listing not found: "+id));}
    @Transactional public DonationListing create(DonationListingRequest r,long userId){validateCutoffs(r.pickupStart(),r.safetyCutoff());return repository.save(new DonationListing(r.foodName().trim(),r.description().trim(),r.donorOrganization().trim(),r.pickupAddress().trim(),r.quantity(),r.pickupStart(),r.safetyCutoff(),r.dietaryTags(),userId));}
    @Transactional public DonationListing update(long id,DonationListingRequest r,long userId){DonationListing d=get(id);if(!d.getCreatedByUserId().equals(userId))throw new org.springframework.security.access.AccessDeniedException("Only the listing creator can update it");if(d.getStatus()!=DonationStatus.AVAILABLE)throw new IllegalStateException("Only available donations can be edited");validateCutoffs(r.pickupStart(),r.safetyCutoff());d.setFoodName(r.foodName().trim());d.setDescription(r.description().trim());d.setDonorOrganization(r.donorOrganization().trim());d.setPickupAddress(r.pickupAddress().trim());d.setQuantity(r.quantity());d.setPickupStart(r.pickupStart());d.setSafetyCutoff(r.safetyCutoff());d.setDietaryTags(r.dietaryTags());return repository.save(d);}
    @Transactional public void withdraw(long id,long userId){DonationListing d=get(id);if(!d.getCreatedByUserId().equals(userId))throw new org.springframework.security.access.AccessDeniedException("Only the listing creator can withdraw it");if(d.getStatus()==DonationStatus.CLAIMED||d.getStatus()==DonationStatus.PICKED_UP)throw new IllegalStateException("Claimed or collected donations cannot be withdrawn");d.setStatus(DonationStatus.WITHDRAWN);}
    private void validateCutoffs(LocalDateTime pickupStart,LocalDateTime cutoff){if(!pickupStart.isBefore(cutoff))throw new IllegalArgumentException("Pickup must begin before the safety cutoff");}
}
