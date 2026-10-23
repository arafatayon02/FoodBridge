package com.foodrescue.consumer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter @Setter @NoArgsConstructor
public class CustomerOrder {
    public enum Status { PLACED, READY, COMPLETED, CANCELLED, REFUNDED, PARTIALLY_REFUNDED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long consumerId;
    private Long storeId;
    private String listingTitle;
    private Integer quantity;
    private BigDecimal originalTotal;
    private BigDecimal paidTotal;
    private BigDecimal refundedAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PLACED;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime completedAt;
}
