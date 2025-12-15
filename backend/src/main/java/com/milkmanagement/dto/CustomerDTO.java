package com.milkmanagement.dto;

import com.milkmanagement.entity.Customer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Long id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String address;
    
    @NotBlank(message = "Mobile number is required")
    private String mobileNumber;
    
    @Email(message = "Email should be valid")
    private String email;
    
    @NotNull(message = "Daily milk quantity is required")
    @PositiveOrZero(message = "Daily milk quantity must be positive or zero")
    private BigDecimal dailyMilkQuantity;
    
    private Customer.MilkType milkType;
    
    private Customer.DeliveryStatus deliveryStatus;
    
    private Long createdById;
}
