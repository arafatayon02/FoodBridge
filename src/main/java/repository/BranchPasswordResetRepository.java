package com.foodbridge.backend.repository;
import com.foodbridge.backend.entity.BranchPasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface BranchPasswordResetRepository extends JpaRepository<BranchPasswordReset,Long> {
    Optional<BranchPasswordReset> findFirstByEmailAndUsedFalseOrderByIdDesc(String email);
//
}
