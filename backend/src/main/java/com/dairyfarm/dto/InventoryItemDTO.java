package com.dairyfarm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItemDTO {
    private Long id;
    private String name;
    private String category;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal costPerUnit;
    private BigDecimal totalCost;
    private String supplierName;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private BigDecimal lowStockThreshold;
    private Boolean isLowStock;
    private String notes;
}

