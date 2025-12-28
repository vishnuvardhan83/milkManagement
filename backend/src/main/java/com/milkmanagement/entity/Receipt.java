package com.milkmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "receipts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "receipt_number", unique = true, nullable = false)
    private String receiptNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Column(name = "receipt_date", nullable = false)
    private LocalDate receiptDate;
    
    @Column(name = "quantity_liters", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityLiters;
    
    @Column(name = "milk_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal milkRate;
    
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;
    
    @Column(name = "paid_amount", precision = 10, scale = 2)
    private BigDecimal paidAmount;
    
    @Column(name = "pending_amount", precision = 10, scale = 2)
    private BigDecimal pendingAmount;
    
    @Column(name = "payment_date")
    private LocalDate paymentDate;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
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
        calculateAmounts();
        if (receiptNumber == null || receiptNumber.isEmpty()) {
            receiptNumber = generateReceiptNumber();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateAmounts();
    }
    
    private void calculateAmounts() {
        if (quantityLiters != null && milkRate != null) {
            totalAmount = quantityLiters.multiply(milkRate);
        } else {
            totalAmount = BigDecimal.ZERO;
        }
        
        if (paidAmount == null) {
            paidAmount = BigDecimal.ZERO;
        }
        
        pendingAmount = totalAmount.subtract(paidAmount);
        
        if (pendingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            paymentStatus = PaymentStatus.PAID;
        } else if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            paymentStatus = PaymentStatus.PARTIAL;
        } else {
            paymentStatus = PaymentStatus.PENDING;
        }
    }
    
    private String generateReceiptNumber() {
        return "RCP-" + System.currentTimeMillis();
    }
    
    public enum PaymentStatus {
        PENDING, PARTIAL, PAID
    }
}

