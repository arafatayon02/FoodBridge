package com.foodbridge.entity;
import jakarta.persistence.*;import lombok.*;import java.time.*;
@Entity @Getter @Setter @NoArgsConstructor public class DiscountOffer {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String shopName;
    @Column(nullable=false) private String productName;
    @Column(nullable=false) private int discountPercent;
    @Column(nullable=false) private LocalDateTime expiresAt;
    private boolean active=true;
}

