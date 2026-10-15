package com.foodbridge.admin.repo;
import com.foodbridge.admin.model.BillingStatement; import org.springframework.data.jpa.repository.JpaRepository;
public interface BillingStatementRepository extends JpaRepository<BillingStatement,Long>{ }
