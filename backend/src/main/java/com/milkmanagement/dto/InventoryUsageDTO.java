package com.milkmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryUsageDTO {
    private Long id;
    private Long inventoryItemId;
    private String inventoryItemName;
    private LocalDate usageDate;
    private BigDecimal quantityUsed;
    private String purpose;
}

