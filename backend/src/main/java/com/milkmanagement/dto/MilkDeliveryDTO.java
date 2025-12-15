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
public class MilkDeliveryDTO {
    private Long id;
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    @NotNull(message = "Delivery date is required")
    private LocalDate deliveryDate;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private BigDecimal quantityDelivered;
    
    @NotNull(message = "Price per unit is required")
    @Positive(message = "Price per unit must be positive")
    private BigDecimal pricePerUnit;
    
    private BigDecimal totalAmount;
    
    private Long deliveredById;
    
    private String notes;
    
    private String customerName;
    private String productName;
}
