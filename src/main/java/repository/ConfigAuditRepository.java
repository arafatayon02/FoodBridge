package com.foodrescue.consumer.repository;

import com.foodrescue.consumer.model.ConfigAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfigAuditRepository extends JpaRepository<ConfigAudit, Long> {
    List<ConfigAudit> findByConfigKeyOrderByChangedAtDesc(String key);
    List<ConfigAudit> findTop100ByOrderByChangedAtDesc();
}
