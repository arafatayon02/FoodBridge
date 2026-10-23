package com.foodrescue.retail.repository;

import com.foodrescue.retail.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByStoreIdOrderByCreatedAtDesc(Long storeId);
    List<Notification> findByStoreIdAndSeenFalseOrderByCreatedAtDesc(Long storeId);
}
