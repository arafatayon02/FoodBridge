package com.foodrescue.consumer.repository;

import com.foodrescue.consumer.model.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemConfigRepository extends JpaRepository<SystemConfig, String> {
}
