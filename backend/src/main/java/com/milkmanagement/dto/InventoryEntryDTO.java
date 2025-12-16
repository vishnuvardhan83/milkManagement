package com.milkmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryEntryDTO {
    private Long id;
    private Long productId;
    private String productName;
    private LocalDate entryDate;
    private BigDecimal totalLitersReceived;
    private BigDecimal pricePerLitre;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

