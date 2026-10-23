package com.foodbridge.admin.model;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.Instant;
@Entity @Table(name="retail_transactions")
public class RetailTransaction {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String businessName;
    @Column(nullable=false,precision=14,scale=2) private BigDecimal grossAmount;
    @Column(nullable=false,precision=14,scale=2) private BigDecimal commissionAmount;
    @Column(nullable=false) private Instant occurredAt=Instant.now();
    protected RetailTransaction(){} public RetailTransaction(String business,BigDecimal gross,BigDecimal commission){businessName=business;grossAmount=gross;commissionAmount=commission;}
    public Long getId(){return id;} public String getBusinessName(){return businessName;} public BigDecimal getGrossAmount(){return grossAmount;} public BigDecimal getCommissionAmount(){return commissionAmount;} public Instant getOccurredAt(){return occurredAt;}
}
