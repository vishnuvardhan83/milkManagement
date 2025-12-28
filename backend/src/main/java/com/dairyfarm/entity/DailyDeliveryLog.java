package com.dairyfarm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_delivery_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyDeliveryLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_boy_id")
    private User deliveryBoy;
    
    @Column(name = "delivery_date", nullable = false)
    private LocalDate deliveryDate;
    
    @Column(name = "scheduled_time", length = 10)
    private String scheduledTime;
    
    @Column(name = "actual_delivery_time")
    private LocalDateTime actualDeliveryTime;
    
    @Column(name = "products_delivered_json", columnDefinition = "TEXT")
    private String productsDeliveredJson;
    
    @Column(name = "quantity_delivered", precision = 10, scale = 2)
    private BigDecimal quantityDelivered;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false, length = 20)
    private DeliveryStatus deliveryStatus;
    
    @Column(name = "qr_code", length = 500)
    private String qrCode;
    
    @Column(name = "qr_scanned")
    private Boolean qrScanned = false;
    
    @Column(name = "qr_scan_time")
    private LocalDateTime qrScanTime;
    
    @Column(name = "customer_signature", length = 500)
    private String customerSignature;
    
    @Column(name = "delivery_notes", columnDefinition = "TEXT")
    private String deliveryNotes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (deliveryStatus == null) {
            deliveryStatus = DeliveryStatus.PENDING;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum DeliveryStatus {
        PENDING, ASSIGNED, IN_TRANSIT, DELIVERED, FAILED, CANCELLED
    }
}

