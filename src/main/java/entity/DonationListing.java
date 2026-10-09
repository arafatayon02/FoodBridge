package com.foodbridge.backend.entity;
import jakarta.persistence.*;import lombok.*;import java.time.*;
@Entity @Getter @Setter @NoArgsConstructor public class DonationListing {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false) private RestaurantDonor donor;
    @Column(nullable=false) private String title;
    @Column(nullable=false,length=1000) private String description;
    @Column(nullable=false) private int portions;
    @Column(nullable=false) private String pickupAddress;
    @Column(nullable=false) private LocalDateTime pickupBefore;
    @Enumerated(EnumType.STRING) private Status status=Status.AVAILABLE;
    public enum Status {AVAILABLE,CLAIMED,CANCELLED}
}
