package com.foodbridge.payment;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction,Long>{Optional<PaymentTransaction> findByProviderReference(String ref);Optional<PaymentTransaction> findFirstByOrderIdOrderByIdDesc(Long orderId);}

