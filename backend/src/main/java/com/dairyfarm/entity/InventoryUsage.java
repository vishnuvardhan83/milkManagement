package com.dairyfarm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_usage")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryUsage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id", nullable = false)
    private InventoryItem inventoryItem;
    
    @Column(name = "usage_date", nullable = false)
    private LocalDate usageDate;
    
    @Column(name = "quantity_used", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityUsed;
    
    @Column(name = "purpose", columnDefinition = "TEXT")
    private String purpose;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "used_by")
    private User usedBy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

