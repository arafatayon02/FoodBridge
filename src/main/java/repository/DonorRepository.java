package com.foodbridge.backend.repository;

import com.foodbridge.backend.entity.DonorStatus;
import com.foodbridge.backend.entity.RestaurantDonor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonorRepository extends JpaRepository<RestaurantDonor, Long> {

    List<RestaurantDonor> findByStatus(DonorStatus status);
}
