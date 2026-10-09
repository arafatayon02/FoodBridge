package com.foodbridge.backend.entity;
import jakarta.persistence.*;import lombok.*;
@Entity @Getter @Setter @NoArgsConstructor public class CharityApplication {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String name;
    @Column(nullable=false,unique=true) private String email;
    @Column(nullable=false) private String address;
    @Column(nullable=false) private String registrationNumber;
    @Enumerated(EnumType.STRING) private Status status=Status.PENDING;
    public enum Status {PENDING,APPROVED,REJECTED}
}
