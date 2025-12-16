package com.milkmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStatusDTO {
    private Long productId;
    private String productName;
    private LocalDate date;
    private BigDecimal totalReceived;
    private BigDecimal available;
    private BigDecimal pricePerLitre;
}
