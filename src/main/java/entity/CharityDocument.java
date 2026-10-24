package com.foodbridge.charity;
import jakarta.persistence.*;import java.time.LocalDateTime;
@Entity @Table(name="charity_documents") public class CharityDocument {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,length=190) private String uploadedBy;
    @Column(nullable=false,length=255) private String originalFilename;
    @Column(nullable=false,length=255,unique=true) private String storageFilename;
    @Column(nullable=false,length=100) private String contentType;
    @Column(nullable=false) private long sizeBytes;
    @Column(nullable=false,length=30) private String status="PENDING_REVIEW";
    @Column(nullable=false) private LocalDateTime uploadedAt=LocalDateTime.now();
    protected CharityDocument(){}
    public CharityDocument(String by,String original,String stored,String type,long size){uploadedBy=by;originalFilename=original;storageFilename=stored;contentType=type;sizeBytes=size;}
    public Long getId(){return id;}public String getUploadedBy(){return uploadedBy;}public String getOriginalFilename(){return originalFilename;}public String getStorageFilename(){return storageFilename;}public String getContentType(){return contentType;}public long getSizeBytes(){return sizeBytes;}public String getStatus(){return status;}public LocalDateTime getUploadedAt(){return uploadedAt;}public void setStatus(String s){status=s;}
}
