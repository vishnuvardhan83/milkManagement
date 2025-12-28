package com.dairyfarm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private ExpenseCategory category;
    
    @Column(name = "description", nullable = false, length = 255)
    private String description;
    
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;
    
    @Column(name = "receipt_number", length = 100)
    private String receiptNumber;
    
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
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum ExpenseCategory {
        FEED, MEDICINE, LABOR, MAINTENANCE, UTILITIES, TRANSPORT, INSURANCE, OTHER
    }
}

