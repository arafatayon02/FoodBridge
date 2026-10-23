package com.foodrescue.consumer.service;

import com.foodrescue.consumer.dto.OrderRequest;
import com.foodrescue.consumer.exception.NotFoundException;
import com.foodrescue.consumer.model.Consumer;
import com.foodrescue.consumer.model.CustomerOrder;
import com.foodrescue.consumer.model.Store;
import com.foodrescue.consumer.repository.ConsumerRepository;
import com.foodrescue.consumer.repository.OrderRepository;
import com.foodrescue.consumer.repository.StoreRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orders;
    private final ConsumerRepository consumers;
    private final StoreRepository stores;
    private final ConfigService config;

    @Transactional
    public CustomerOrder place(OrderRequest r) {
        if (config.getBoolean(ConfigService.MAINTENANCE_MODE)) {
            throw new IllegalStateException("Platform is in maintenance mode - new orders are disabled");
        }
        Consumer c = consumers.findById(r.consumerId())
                .orElseThrow(() -> new NotFoundException("Consumer " + r.consumerId() + " not found"));
        if (!c.isActive()) throw new IllegalStateException("Consumer account is deactivated");
        Store s = stores.findById(r.storeId())
                .orElseThrow(() -> new NotFoundException("Store " + r.storeId() + " not found"));
        if (s.getVerificationStatus() == Store.VerificationStatus.SUSPENDED) {
            throw new IllegalStateException("Store is suspended");
        }
        if (r.quantity() > config.getInt(ConfigService.MAX_QTY_PER_ORDER)) {
            throw new IllegalArgumentException("Quantity exceeds the maximum allowed per order");
        }
        if (r.paidTotal().compareTo(r.originalTotal()) > 0) {
            throw new IllegalArgumentException("paidTotal cannot exceed originalTotal");
        }
        CustomerOrder o = new CustomerOrder();
        o.setConsumerId(r.consumerId());
        o.setStoreId(r.storeId());
        o.setListingTitle(r.listingTitle());
        o.setQuantity(r.quantity());
        o.setOriginalTotal(r.originalTotal());
        o.setPaidTotal(r.paidTotal());
        return orders.save(o);
    }

    @Transactional
    public CustomerOrder complete(Long id) {
        CustomerOrder o = get(id);
        if (o.getStatus() != CustomerOrder.Status.PLACED && o.getStatus() != CustomerOrder.Status.READY) {
            throw new IllegalStateException("Order cannot be completed from status " + o.getStatus());
        }
        o.setStatus(CustomerOrder.Status.COMPLETED);
        o.setCompletedAt(LocalDateTime.now());
        return orders.save(o);
    }

    @Transactional(readOnly = true)
    public CustomerOrder get(Long id) {
        return orders.findById(id).orElseThrow(() -> new NotFoundException("Order " + id + " not found"));
    }

    /** FOOD-54: Build Consumer Order History (filter by status / date range, newest first, paged). */
    @Transactional(readOnly = true)
    public Page<CustomerOrder> history(Long consumerId, CustomerOrder.Status status,
                                       LocalDateTime from, LocalDateTime to, int page, int size) {
        if (!consumers.existsById(consumerId)) throw new NotFoundException("Consumer " + consumerId + " not found");
        Specification<CustomerOrder> spec = (root, q, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            ps.add(cb.equal(root.get("consumerId"), consumerId));
            if (status != null) ps.add(cb.equal(root.get("status"), status));
            if (from != null) ps.add(cb.greaterThanOrEqualTo(root.get("createdAt"), from));
            if (to != null) ps.add(cb.lessThanOrEqualTo(root.get("createdAt"), to));
            return cb.and(ps.toArray(new Predicate[0]));
        };
        return orders.findAll(spec, PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "createdAt")));
    }
}
