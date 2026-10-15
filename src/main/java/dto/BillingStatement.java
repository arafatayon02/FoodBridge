package com.foodbridge.admin.model;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.Instant; import java.time.LocalDate;
@Entity @Table(name="billing_statements",uniqueConstraints=@UniqueConstraint(columnNames={"businessName","periodStart","periodEnd"}))
public class BillingStatement {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String businessName;
    @Column(nullable=false) private LocalDate periodStart;
    @Column(nullable=false) private LocalDate periodEnd;
    @Column(nullable=false,precision=14,scale=2) private BigDecimal grossSales;
    @Column(nullable=false,precision=14,scale=2) private BigDecimal commission;
    @Column(nullable=false,precision=14,scale=2) private BigDecimal netSettlement;
    @Column(nullable=false) private String status="GENERATED";
    @Column(nullable=false) private Instant generatedAt=Instant.now();
    protected BillingStatement(){}
    public BillingStatement(String business,LocalDate start,LocalDate end,BigDecimal gross,BigDecimal commission){this.businessName=business;this.periodStart=start;this.periodEnd=end;this.grossSales=gross;this.commission=commission;this.netSettlement=gross.subtract(commission);}
    public Long getId(){return id;} public String getBusinessName(){return businessName;} public LocalDate getPeriodStart(){return periodStart;} public LocalDate getPeriodEnd(){return periodEnd;} public BigDecimal getGrossSales(){return grossSales;} public BigDecimal getCommission(){return commission;} public BigDecimal getNetSettlement(){return netSettlement;} public String getStatus(){return status;} public Instant getGeneratedAt(){return generatedAt;}
}
