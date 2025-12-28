package com.milkmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_milk_collections",
       uniqueConstraints = @UniqueConstraint(columnNames = {"collection_date", "animal_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyMilkCollection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;
    
    @Column(name = "collection_date", nullable = false)
    private LocalDate collectionDate;
    
    @Column(name = "morning_quantity", precision = 10, scale = 2)
    private BigDecimal morningQuantity;
    
    @Column(name = "evening_quantity", precision = 10, scale = 2)
    private BigDecimal eveningQuantity;
    
    @Column(name = "total_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalQuantity;
    
    @Column(name = "quality_grade")
    private String qualityGrade;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collected_by")
    private User collectedBy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateTotal();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateTotal();
    }
    
    private void calculateTotal() {
        BigDecimal morning = morningQuantity != null ? morningQuantity : BigDecimal.ZERO;
        BigDecimal evening = eveningQuantity != null ? eveningQuantity : BigDecimal.ZERO;
        totalQuantity = morning.add(evening);
    }
}

