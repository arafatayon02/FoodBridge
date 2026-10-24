package com.foodbridge.donor;
import jakarta.persistence.*;
@Entity @Table(name="donor_organizations") public class DonorOrganization {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,unique=true,length=140) private String name;
    @Column(nullable=false,length=240) private String address;
    @Column(nullable=false) private boolean verified=false;
    protected DonorOrganization(){}
    public DonorOrganization(String name,String address){this.name=name;this.address=address;}
    public Long getId(){return id;} public String getName(){return name;} public String getAddress(){return address;} public boolean isVerified(){return verified;} public void setVerified(boolean v){verified=v;}
}
