package com.foodbridge.admin.repo;
import com.foodbridge.admin.model.RetailTransaction; import org.springframework.data.jpa.repository.JpaRepository;
public interface RetailTransactionRepository extends JpaRepository<RetailTransaction,Long>{ }
