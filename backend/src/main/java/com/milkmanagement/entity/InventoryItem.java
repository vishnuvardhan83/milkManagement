package com.milkmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private InventoryCategory category;
    
    @Column(name = "quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;
    
    @Column(name = "unit", nullable = false)
    private String unit;
    
    @Column(name = "cost_per_unit", precision = 10, scale = 2)
    private BigDecimal costPerUnit;
    
    @Column(name = "total_cost", precision = 10, scale = 2)
    private BigDecimal totalCost;
    
    @Column(name = "supplier_name")
    private String supplierName;
    
    @Column(name = "purchase_date")
    private java.time.LocalDate purchaseDate;
    
    @Column(name = "expiry_date")
    private java.time.LocalDate expiryDate;
    
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
        calculateTotalCost();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateTotalCost();
    }
    
    private void calculateTotalCost() {
        if (quantity != null && costPerUnit != null) {
            totalCost = quantity.multiply(costPerUnit);
        } else {
            totalCost = BigDecimal.ZERO;
        }
    }
    
    public enum InventoryCategory {
        FODDER, MEDICINE, PACKAGING, CANS, SUPPLEMENTS, EQUIPMENT, OTHER
    }
}

