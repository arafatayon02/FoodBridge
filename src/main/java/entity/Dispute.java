package com.foodrescue.consumer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "disputes")
@Getter @Setter @NoArgsConstructor
public class Dispute {
    public enum Reason { FOOD_QUALITY, WRONG_ITEM, NOT_AVAILABLE, SAFETY_CONCERN, OTHER }
    public enum Status { OPEN, UNDER_REVIEW, RESOLVED, REJECTED }
    public enum Decision { REFUND_FULL, REFUND_PARTIAL, REJECT }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private Long consumerId;

    @Enumerated(EnumType.STRING)
    private Reason reason;
    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status = Status.OPEN;
    @Enumerated(EnumType.STRING)
    private Decision decision;

    private BigDecimal refundAmount = BigDecimal.ZERO;
    @Column(length = 1000)
    private String resolutionNote;
    private String resolvedBy;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime resolvedAt;
}
