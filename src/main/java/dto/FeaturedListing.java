package com.foodbridge.admin.model;
import jakarta.persistence.*; import java.time.Instant; import java.time.LocalDate;
@Entity @Table(name="featured_listings")
public class FeaturedListing {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String businessName;
    @Column(nullable=false) private String listingTitle;
    @Column(nullable=false) private Long subscriptionPlanId;
    @Column(nullable=false) private LocalDate startDate;
    @Column(nullable=false) private LocalDate endDate;
    @Column(nullable=false) private boolean active=true;
    @Column(nullable=false) private Instant createdAt=Instant.now();
    protected FeaturedListing(){} public FeaturedListing(String business,String title,Long plan,LocalDate start,LocalDate end){this.businessName=business;this.listingTitle=title;this.subscriptionPlanId=plan;this.startDate=start;this.endDate=end;this.active=true;}
    public Long getId(){return id;} public String getBusinessName(){return businessName;} public String getListingTitle(){return listingTitle;} public Long getSubscriptionPlanId(){return subscriptionPlanId;} public LocalDate getStartDate(){return startDate;} public LocalDate getEndDate(){return endDate;} public boolean isActive(){return active;} public Instant getCreatedAt(){return createdAt;}
    public void setActive(boolean active){this.active=active;}
}
