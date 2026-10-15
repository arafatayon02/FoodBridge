package com.foodbridge.supershop.repository;
import com.foodbridge.supershop.entity.DiscountListing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.*;
public interface DiscountListingRepository extends JpaRepository<DiscountListing,Long> {
    List<DiscountListing> findByActiveTrueAndExpiryDateGreaterThanEqualOrderByExpiryDateAsc(LocalDate date);
    boolean existsBySupershop_IdAndSkuIgnoreCase(Long id,String sku);
}
