package com.foodrescue.consumer.repository;

import com.foodrescue.consumer.model.Dispute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface DisputeRepository extends JpaRepository<Dispute, Long> {
    Page<Dispute> findByStatus(Dispute.Status status, Pageable pageable);
    List<Dispute> findByConsumerIdOrderByCreatedAtDesc(Long consumerId);
    boolean existsByOrderIdAndStatusIn(Long orderId, Collection<Dispute.Status> statuses);
}
