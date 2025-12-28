package com.milkmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Column(name = "category", nullable = false)
    private ExpenseCategory category;
    
    @Column(name = "description", nullable = false)
    private String description;
    
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal amount;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @Column(name = "receipt_number")
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

