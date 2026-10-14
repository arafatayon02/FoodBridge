package com.foodbridge.supershop.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity @Table(name="discount_listings")
public class DiscountListing {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false,fetch=FetchType.LAZY) private Supershop supershop;
    @Column(nullable=false) private String productName;
    @Column(nullable=false) private String sku;
    @Column(nullable=false,precision=12,scale=2) private BigDecimal originalPrice;
    @Column(nullable=false,precision=5,scale=2) private BigDecimal discountPercent;
    @Column(nullable=false,precision=12,scale=2) private BigDecimal discountedPrice;
    @Column(nullable=false) private int quantity;
    @Column(nullable=false) private LocalDate expiryDate;
    @Column(nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
    private boolean active=true;
    protected DiscountListing(){}
    public DiscountListing(Supershop shop,String name,String sku,BigDecimal originalPrice,BigDecimal discountPercent,int quantity,LocalDate expiryDate){
        this.supershop=shop;this.productName=name.trim();this.sku=sku.trim();this.originalPrice=originalPrice;
        this.discountPercent=discountPercent;this.discountedPrice=originalPrice.multiply(BigDecimal.ONE.subtract(discountPercent.divide(new BigDecimal("100")))).setScale(2,java.math.RoundingMode.HALF_UP);
        this.quantity=quantity;this.expiryDate=expiryDate;
    }
    public Long getId(){return id;} public Long getSupershopId(){return supershop.getId();} public String getBusinessName(){return supershop.getBusinessName();}
    public String getProductName(){return productName;} public String getSku(){return sku;} public BigDecimal getOriginalPrice(){return originalPrice;}
    public BigDecimal getDiscountPercent(){return discountPercent;} public BigDecimal getDiscountedPrice(){return discountedPrice;}
    public int getQuantity(){return quantity;} public LocalDate getExpiryDate(){return expiryDate;} public LocalDateTime getCreatedAt(){return createdAt;}
    public boolean isActive(){return active;} public void setActive(boolean active){this.active=active;}
}
