package com.foodbridge.backend.repository;

import com.foodbridge.backend.entity.Supershop;
import com.foodbridge.backend.entity.SupershopStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupershopRepository extends JpaRepository<Supershop, Long> {

    List<Supershop> findByStatus(SupershopStatus status);
}
