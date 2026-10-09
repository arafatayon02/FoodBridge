package com.foodbridge.backend.repository;
import com.foodbridge.backend.entity.*;import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface DonationListingRepository extends JpaRepository<DonationListing,Long> {
    List<DonationListing> findByStatus(DonationListing.Status status);
    List<DonationListing> findByDonorId(Long donorId);
}
