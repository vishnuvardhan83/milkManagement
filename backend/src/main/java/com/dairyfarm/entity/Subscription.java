package com.dairyfarm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;
    
    @Column(name = "subscription_number", unique = true, nullable = false, length = 100)
    private String subscriptionNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_plan_id", nullable = false)
    private SubscriptionPlan subscriptionPlan;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Column(name = "next_billing_date")
    private LocalDate nextBillingDate;
    
    @Column(name = "monthly_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SubscriptionStatus status;
    
    @Column(name = "auto_renew")
    private Boolean autoRenew = true;
    
    @Column(name = "paused")
    private Boolean paused = false;
    
    @Column(name = "pause_start_date")
    private LocalDate pauseStartDate;
    
    @Column(name = "pause_end_date")
    private LocalDate pauseEndDate;
    
    @Column(name = "wallet_balance", precision = 10, scale = 2)
    private BigDecimal walletBalance = BigDecimal.ZERO;
    
    @Column(name = "loyalty_points")
    private Integer loyaltyPoints = 0;
    
    @Column(name = "coupon_code", length = 50)
    private String couponCode;
    
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @Column(name = "products_json", columnDefinition = "TEXT")
    private String productsJson;
    
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
        if (subscriptionNumber == null || subscriptionNumber.isEmpty()) {
            subscriptionNumber = "SUB-" + System.currentTimeMillis();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum SubscriptionStatus {
        ACTIVE, PAUSED, CANCELLED, EXPIRED, PENDING_PAYMENT
    }
}

