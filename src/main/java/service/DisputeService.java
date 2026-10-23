package com.foodrescue.consumer.service;

import com.foodrescue.consumer.dto.DisputeRequest;
import com.foodrescue.consumer.dto.DisputeResolution;
import com.foodrescue.consumer.exception.NotFoundException;
import com.foodrescue.consumer.model.CustomerOrder;
import com.foodrescue.consumer.model.Dispute;
import com.foodrescue.consumer.repository.DisputeRepository;
import com.foodrescue.consumer.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** FOOD-88: Implement Dispute & Refund Management. */
@Service
@RequiredArgsConstructor
@Slf4j
public class DisputeService {

    private final DisputeRepository disputes;
    private final OrderRepository orders;
    private final ConfigService config;

    @Transactional
    public Dispute open(DisputeRequest r) {
        CustomerOrder o = orders.findById(r.orderId())
                .orElseThrow(() -> new NotFoundException("Order " + r.orderId() + " not found"));
        if (!o.getConsumerId().equals(r.consumerId())) {
            throw new IllegalArgumentException("Order does not belong to this consumer");
        }
        if (o.getStatus() != CustomerOrder.Status.COMPLETED) {
            throw new IllegalStateException("Only completed orders can be disputed");
        }
        LocalDateTime base = o.getCompletedAt() != null ? o.getCompletedAt() : o.getCreatedAt();
        int windowDays = config.getInt(ConfigService.DISPUTE_WINDOW_DAYS);
        if (base.plusDays(windowDays).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Dispute window of " + windowDays + " day(s) has passed");
        }
        if (disputes.existsByOrderIdAndStatusIn(o.getId(), List.of(Dispute.Status.OPEN, Dispute.Status.UNDER_REVIEW))) {
            throw new IllegalStateException("An open dispute already exists for this order");
        }
        Dispute d = new Dispute();
        d.setOrderId(o.getId());
        d.setConsumerId(r.consumerId());
        d.setReason(r.reason());
        d.setDescription(r.description());
        return disputes.save(d);
    }

    @Transactional(readOnly = true)
    public Dispute get(Long id) {
        return disputes.findById(id).orElseThrow(() -> new NotFoundException("Dispute " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<Dispute> forConsumer(Long consumerId) {
        return disputes.findByConsumerIdOrderByCreatedAtDesc(consumerId);
    }

    @Transactional(readOnly = true)
    public Page<Dispute> queue(Dispute.Status status, int page, int size) {
        PageRequest pr = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.ASC, "createdAt"));
        return disputes.findByStatus(status == null ? Dispute.Status.OPEN : status, pr);
    }

    @Transactional
    public Dispute startReview(Long id) {
        Dispute d = get(id);
        if (d.getStatus() != Dispute.Status.OPEN) throw new IllegalStateException("Dispute is not OPEN");
        d.setStatus(Dispute.Status.UNDER_REVIEW);
        return disputes.save(d);
    }

    @Transactional
    public Dispute resolve(Long id, DisputeResolution r) {
        Dispute d = get(id);
        if (d.getStatus() == Dispute.Status.RESOLVED || d.getStatus() == Dispute.Status.REJECTED) {
            throw new IllegalStateException("Dispute already closed");
        }
        CustomerOrder o = orders.findById(d.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order " + d.getOrderId() + " not found"));
        BigDecimal refundable = o.getPaidTotal().subtract(o.getRefundedAmount());
        BigDecimal refund = BigDecimal.ZERO;

        switch (r.decision()) {
            case REFUND_FULL -> {
                refund = refundable;
                o.setStatus(CustomerOrder.Status.REFUNDED);
                d.setStatus(Dispute.Status.RESOLVED);
            }
            case REFUND_PARTIAL -> {
                if (r.refundAmount() == null || r.refundAmount().signum() <= 0) {
                    throw new IllegalArgumentException("refundAmount is required for a partial refund");
                }
                if (r.refundAmount().compareTo(refundable) > 0) {
                    throw new IllegalArgumentException("refundAmount exceeds refundable balance (" + refundable + ")");
                }
                refund = r.refundAmount();
                o.setStatus(refund.compareTo(refundable) == 0
                        ? CustomerOrder.Status.REFUNDED : CustomerOrder.Status.PARTIALLY_REFUNDED);
                d.setStatus(Dispute.Status.RESOLVED);
            }
            case REJECT -> d.setStatus(Dispute.Status.REJECTED);
        }
        o.setRefundedAmount(o.getRefundedAmount().add(refund));
        d.setDecision(r.decision());
        d.setRefundAmount(refund);
        d.setResolutionNote(r.note());
        d.setResolvedBy(r.resolvedBy());
        d.setResolvedAt(LocalDateTime.now());
        orders.save(o);
        if (refund.signum() > 0) {
            // Hook: call payment gateway refund API here.
            log.info("Refund of {} issued for order {} (dispute {})", refund, o.getId(), d.getId());
        }
        return disputes.save(d);
    }
}
