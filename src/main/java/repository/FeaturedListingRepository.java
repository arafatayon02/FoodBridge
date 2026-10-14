package com.foodbridge.admin.repo;
import com.foodbridge.admin.model.FeaturedListing; import org.springframework.data.jpa.repository.JpaRepository;
public interface FeaturedListingRepository extends JpaRepository<FeaturedListing,Long>{ long countByActiveTrue(); }
