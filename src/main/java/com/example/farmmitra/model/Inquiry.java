package com.example.farmmitra.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inquiries")
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private Buyer buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;
    
    private Integer requiredQuantity; // ⬅️ I've added this, as it's required for your form
    private Double negotiatedRate;    // ⬅️ I've added this too
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InquiryStatus status;

    private LocalDateTime inquiryDate;
    
    @PrePersist
    protected void onCreate() {
        this.inquiryDate = LocalDateTime.now();
        if (this.status == null) { // Only set to PENDING if not already set
            this.status = InquiryStatus.PENDING;
        }
    }


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public Crop getCrop() {
        return crop;
    }

    public void setCrop(Crop crop) {
        this.crop = crop;
    }

    public Integer getRequiredQuantity() {
        return requiredQuantity;
    }

    public void setRequiredQuantity(Integer requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }

    public Double getNegotiatedRate() {
        return negotiatedRate;
    }

    public void setNegotiatedRate(Double negotiatedRate) {
        this.negotiatedRate = negotiatedRate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public InquiryStatus getStatus() {
        return status;
    }

    public void setStatus(InquiryStatus status) {
        this.status = status;
    }

    public LocalDateTime getInquiryDate() {
        return inquiryDate;
    }

    public void setInquiryDate(LocalDateTime inquiryDate) {
        this.inquiryDate = inquiryDate;
    }
}