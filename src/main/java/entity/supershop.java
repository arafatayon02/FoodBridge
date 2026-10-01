package com.foodbridge.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "supershop")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supershop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String businessName;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String contactPerson;

    @Column(nullable = false)
    private String contactPhone;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private SupershopStatus status = SupershopStatus.PENDING;

    // set when an admin approves/rejects
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private String rejectionReason;

    @OneToMany(mappedBy = "supershop", cascade = CascadeType.ALL)
    @Builder.Default
    private List<User> staff = new ArrayList<>();

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
