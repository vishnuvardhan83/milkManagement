package com.dairyfarm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "farms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Farm {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", unique = true, nullable = false, length = 50)
    private String tenantId;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "gst_number", length = 50)
    private String gstNumber;
    
    @Column(name = "logo_url", length = 500)
    private String logoUrl;
    
    @Column(name = "subscription_enabled")
    private Boolean subscriptionEnabled = true;
    
    @Column(name = "delivery_enabled")
    private Boolean deliveryEnabled = true;
    
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
}

