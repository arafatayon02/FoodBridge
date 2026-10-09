package com.foodbridge.backend.repository;
import com.foodbridge.backend.entity.CharityApplication;import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface CharityApplicationRepository extends JpaRepository<CharityApplication,Long> {
    boolean existsByEmail(String email);List<CharityApplication> findByStatus(CharityApplication.Status status);
}
