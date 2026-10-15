package com.foodbridge.admin.model;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.Instant;
@Entity @Table(name="subscription_plans")
public class SubscriptionPlan {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,unique=true) private String name;
    @Column(nullable=false,precision=12,scale=2) private BigDecimal price;
    @Column(nullable=false) private int durationDays;
    @Column(nullable=false) private boolean active=true;
    @Column(nullable=false) private Instant updatedAt=Instant.now();
    protected SubscriptionPlan(){} public SubscriptionPlan(String name,BigDecimal price,int days,boolean active){this.name=name;this.price=price;this.durationDays=days;this.active=active;}
    public Long getId(){return id;} public String getName(){return name;} public BigDecimal getPrice(){return price;} public int getDurationDays(){return durationDays;} public boolean isActive(){return active;} public Instant getUpdatedAt(){return updatedAt;}
    public void update(String name,BigDecimal price,int days,boolean active){this.name=name;this.price=price;this.durationDays=days;this.active=active;this.updatedAt=Instant.now();}
}
