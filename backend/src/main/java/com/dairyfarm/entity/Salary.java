package com.dairyfarm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "salaries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Salary {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;
    
    @Column(name = "salary_month", nullable = false)
    private LocalDate salaryMonth;
    
    @Column(name = "base_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseSalary;
    
    @Column(name = "bonus", precision = 10, scale = 2)
    private BigDecimal bonus;
    
    @Column(name = "deductions", precision = 10, scale = 2)
    private BigDecimal deductions;
    
    @Column(name = "net_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal netSalary;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus;
    
    @Column(name = "payment_date")
    private LocalDate paymentDate;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by")
    private User paidBy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (paymentStatus == null) {
            paymentStatus = PaymentStatus.PENDING;
        }
        calculateNetSalary();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateNetSalary();
    }
    
    private void calculateNetSalary() {
        if (baseSalary == null) {
            baseSalary = BigDecimal.ZERO;
        }
        if (bonus == null) {
            bonus = BigDecimal.ZERO;
        }
        if (deductions == null) {
            deductions = BigDecimal.ZERO;
        }
        netSalary = baseSalary.add(bonus).subtract(deductions);
    }
    
    public enum PaymentStatus {
        PENDING, PAID, PARTIAL
    }
}

