package com.dairyfarm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "mobile_number", nullable = false, length = 20)
    private String mobileNumber;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "daily_milk_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyMilkQuantity = BigDecimal.ZERO;
    
    @Column(name = "balance", precision = 10, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;
    
    @Column(name = "wallet_balance", precision = 10, scale = 2)
    private BigDecimal walletBalance = BigDecimal.ZERO;
    
    @Column(name = "loyalty_points")
    private Integer loyaltyPoints = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "milk_type", length = 10)
    private MilkType milkType = MilkType.COW;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", length = 20)
    private DeliveryStatus deliveryStatus = DeliveryStatus.ACTIVE;
    
    @Column(name = "referral_code", unique = true, length = 50)
    private String referralCode;
    
    @Column(name = "referred_by")
    private Long referredBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (referralCode == null || referralCode.isEmpty()) {
            referralCode = "REF-" + System.currentTimeMillis();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum MilkType {
        COW, BUFFALO, BOTH
    }
    
    public enum DeliveryStatus {
        ACTIVE, PAUSED, INACTIVE
    }
}
