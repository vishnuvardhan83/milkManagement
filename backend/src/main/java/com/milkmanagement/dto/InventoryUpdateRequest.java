package com.milkmanagement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryUpdateRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Total liters received is required")
    @Positive(message = "Total liters must be positive")
    private BigDecimal totalLitersReceived;

    @NotNull(message = "Price per litre is required")
    @Positive(message = "Price per litre must be positive")
    private BigDecimal pricePerLitre;
}
