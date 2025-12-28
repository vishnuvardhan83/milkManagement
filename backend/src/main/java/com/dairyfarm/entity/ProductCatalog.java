package com.dairyfarm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_catalog")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCatalog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false, length = 50)
    private ProductType productType;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "unit", nullable = false, length = 50)
    private String unit;
    
    @Column(name = "price_per_unit", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerUnit;
    
    @Column(name = "subscription_price", precision = 10, scale = 2)
    private BigDecimal subscriptionPrice;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @Column(name = "available_for_subscription")
    private Boolean availableForSubscription = true;
    
    @Column(name = "available_for_daily_sale")
    private Boolean availableForDailySale = true;
    
    @Column(name = "active")
    private Boolean active = true;
    
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
    
    public enum ProductType {
        MILK, CURD, GHEE, PANEER, BUTTER, CHEESE, BUTTERMILK, OTHER
    }
}

