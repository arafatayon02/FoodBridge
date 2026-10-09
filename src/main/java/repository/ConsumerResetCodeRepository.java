package com.foodbridge.repository;
import com.foodbridge.entity.ConsumerResetCode;import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ConsumerResetCodeRepository extends JpaRepository<ConsumerResetCode,Long>{
    Optional<ConsumerResetCode> findFirstByEmailAndUsedFalseOrderByIdDesc(String email);
}
