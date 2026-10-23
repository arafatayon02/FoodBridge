package com.foodrescue.retail.service;

import com.foodrescue.retail.exception.NotFoundException;
import com.foodrescue.retail.model.Notification;
import com.foodrescue.retail.model.NotificationType;
import com.foodrescue.retail.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository repo;

    @Transactional
    public Notification notify(Long storeId, Long listingId, NotificationType type, String message) {
        Notification n = new Notification();
        n.setStoreId(storeId);
        n.setListingId(listingId);
        n.setType(type);
        n.setMessage(message);
        log.info("Notify store {}: {}", storeId, message);
        // Hook: plug in email / SMS / push provider here.
        return repo.save(n);
    }

    @Transactional(readOnly = true)
    public List<Notification> forStore(Long storeId, boolean unreadOnly) {
        return unreadOnly ? repo.findByStoreIdAndSeenFalseOrderByCreatedAtDesc(storeId)
                : repo.findByStoreIdOrderByCreatedAtDesc(storeId);
    }

    @Transactional
    public Notification markSeen(Long id) {
        Notification n = repo.findById(id).orElseThrow(() -> new NotFoundException("Notification " + id + " not found"));
        n.setSeen(true);
        return repo.save(n);
    }
}
