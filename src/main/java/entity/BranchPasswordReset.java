package com.foodbridge.backend.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity public class BranchPasswordReset {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(nullable=false) public String email;
    @Column(nullable=false) public String tokenHash;
    @Column(nullable=false) public Instant expiresAt;
    public boolean used;
}
