package com.foodbridge.donor;
import jakarta.persistence.*;import jakarta.validation.constraints.*;import java.time.LocalDateTime;
@Entity @Table(name="donor_staff_accounts",uniqueConstraints=@UniqueConstraint(name="uk_donor_staff_email_org",columnNames={"organization_id","email"}))
public class DonorStaffAccount {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false,fetch=FetchType.EAGER) @JoinColumn(name="organization_id",nullable=false) private DonorOrganization organization;
    @NotBlank @Email @Column(nullable=false,length=190) private String email;
    @NotBlank @Size(max=100) @Column(nullable=false,length=100) private String fullName;
    @NotBlank @Size(max=80) @Column(nullable=false,length=80) private String jobTitle;
    @Column(name="password_hash",nullable=false,length=100) private String passwordHash;
    @Column(nullable=false) private boolean active=true;
    @Column(nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
    protected DonorStaffAccount(){}
    public DonorStaffAccount(DonorOrganization o,String email,String fullName,String jobTitle,String passwordHash){this.organization=o;this.email=email.toLowerCase().trim();this.fullName=fullName.trim();this.jobTitle=jobTitle.trim();this.passwordHash=passwordHash;}
    public Long getId(){return id;}public DonorOrganization getOrganization(){return organization;}public String getEmail(){return email;}public String getFullName(){return fullName;}public String getJobTitle(){return jobTitle;}public String getPasswordHash(){return passwordHash;}public boolean isActive(){return active;}public LocalDateTime getCreatedAt(){return createdAt;}public void setActive(boolean v){active=v;}
}
