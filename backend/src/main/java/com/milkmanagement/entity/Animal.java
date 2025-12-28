package com.milkmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "animals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Animal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tag_number", unique = true, nullable = false)
    private String tagNumber;
    
    @Column(name = "name")
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "animal_type", nullable = false)
    private AnimalType animalType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "breed")
    private Breed breed;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Column(name = "purchase_date")
    private LocalDate purchaseDate;
    
    @Column(name = "purchase_price", precision = 10, scale = 2)
    private java.math.BigDecimal purchasePrice;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AnimalStatus status;
    
    @Column(name = "health_status")
    private String healthStatus;
    
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
        if (status == null) {
            status = AnimalStatus.ACTIVE;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum AnimalType {
        COW, BUFFALO
    }
    
    public enum Breed {
        HOLSTEIN, JERSEY, SAHIWAL, MURRAH, NILI_RAVI, CROSSBREED, OTHER
    }
    
    public enum AnimalStatus {
        ACTIVE, SICK, PREGNANT, DRY, SOLD, DECEASED
    }
}

