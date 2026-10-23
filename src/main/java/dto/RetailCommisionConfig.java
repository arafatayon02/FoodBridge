package com.foodbridge.admin.model;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.Instant;
@Entity @Table(name="retail_commission_config")
public class RetailCommissionConfig {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,precision=5,scale=2) private BigDecimal ratePercent;
    @Column(nullable=false) private boolean active=true;
    @Column(nullable=false) private Instant updatedAt=Instant.now();
    protected RetailCommissionConfig(){} public RetailCommissionConfig(BigDecimal ratePercent,boolean active){this.ratePercent=ratePercent;this.active=active;}
    public Long getId(){return id;} public BigDecimal getRatePercent(){return ratePercent;} public boolean isActive(){return active;} public Instant getUpdatedAt(){return updatedAt;}
    public void update(BigDecimal rate,boolean active){this.ratePercent=rate;this.active=active;this.updatedAt=Instant.now();}
}
