package com.foodbridge.repository;
import com.foodbridge.entity.DiscountOffer;import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface DiscountOfferRepository extends JpaRepository<DiscountOffer,Long>{
    List<DiscountOffer> findByActiveTrueAndExpiresAtAfterOrderByExpiresAtAsc(java.time.LocalDateTime now);
}

