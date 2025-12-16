package com.milkmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    
    @NotBlank(message = "Product name is required")
    private String name;
    
    private String type; // COW_MILK, BUFFALO_MILK, CURD, etc.
    
    // Optional high-level category for UI (e.g., Dairy)
    private String category;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private BigDecimal quantity;
    
    @NotNull(message = "Price per unit is required")
    @Positive(message = "Price must be positive")
    private BigDecimal pricePerUnit;
    
    private String unit;
    
    private String description;
    
    // Optional fields for richer product card / listing
    @Positive(message = "Minimum order quantity must be positive")
    private BigDecimal minOrderQuantity;
    
    private String imageUrl;
}
