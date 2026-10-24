package com.foodbridge.donation;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="donation_listings", indexes={@Index(name="idx_donation_status_cutoff", columnList="status,safety_cutoff")})
public class DonationListing {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @NotBlank @Size(max=160) @Column(nullable=false,length=160) private String foodName;
    @NotBlank @Size(max=2000) @Column(nullable=false,length=2000) private String description;
    @NotBlank @Size(max=120) @Column(nullable=false,length=120) private String donorOrganization;
    @NotBlank @Size(max=300) @Column(nullable=false,length=300) private String pickupAddress;
    @Min(1) @Max(100000) @Column(nullable=false) private int quantity;
    @NotNull @Future @Column(name="pickup_start",nullable=false) private LocalDateTime pickupStart;
    @NotNull @Future @Column(name="safety_cutoff",nullable=false) private LocalDateTime safetyCutoff;
    @ElementCollection(fetch=FetchType.EAGER) @CollectionTable(name="donation_dietary_tags", joinColumns=@JoinColumn(name="listing_id"))
    @Enumerated(EnumType.STRING) @Column(name="tag",nullable=false,length=40) private Set<DietaryTag> dietaryTags = new HashSet<>();
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private DonationStatus status = DonationStatus.AVAILABLE;
    @Column(nullable=false) private LocalDateTime createdAt = LocalDateTime.now();
    @Column(nullable=false) private Long createdByUserId;

    protected DonationListing() {}
    public DonationListing(String foodName,String description,String donorOrganization,String pickupAddress,int quantity,LocalDateTime pickupStart,LocalDateTime safetyCutoff,Set<DietaryTag> tags,Long createdByUserId) {
        this.foodName=foodName;this.description=description;this.donorOrganization=donorOrganization;this.pickupAddress=pickupAddress;this.quantity=quantity;this.pickupStart=pickupStart;this.safetyCutoff=safetyCutoff;this.dietaryTags=tags==null?new HashSet<>():new HashSet<>(tags);this.createdByUserId=createdByUserId;
    }
    public boolean isAvailableNow(){return status==DonationStatus.AVAILABLE && LocalDateTime.now().isBefore(safetyCutoff) && quantity>0;}
    public void markExpiredIfNeeded(){if(status==DonationStatus.AVAILABLE && !LocalDateTime.now().isBefore(safetyCutoff))status=DonationStatus.EXPIRED;}
    public Long getId(){return id;} public String getFoodName(){return foodName;} public String getDescription(){return description;} public String getDonorOrganization(){return donorOrganization;} public String getPickupAddress(){return pickupAddress;} public int getQuantity(){return quantity;} public LocalDateTime getPickupStart(){return pickupStart;} public LocalDateTime getSafetyCutoff(){return safetyCutoff;} public Set<DietaryTag> getDietaryTags(){return dietaryTags;} public DonationStatus getStatus(){return status;} public LocalDateTime getCreatedAt(){return createdAt;} public Long getCreatedByUserId(){return createdByUserId;}
    public void setFoodName(String v){foodName=v;} public void setDescription(String v){description=v;} public void setDonorOrganization(String v){donorOrganization=v;} public void setPickupAddress(String v){pickupAddress=v;} public void setQuantity(int v){quantity=v;} public void setPickupStart(LocalDateTime v){pickupStart=v;} public void setSafetyCutoff(LocalDateTime v){safetyCutoff=v;} public void setDietaryTags(Set<DietaryTag> v){dietaryTags=new HashSet<>(v==null?Set.of():v);} public void setStatus(DonationStatus v){status=v;}
}
