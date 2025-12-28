package com.dairyfarm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Long id;
    private String name;
    private String address;
    private String mobileNumber;
    private String email;
    private BigDecimal dailyMilkQuantity;
    private BigDecimal balance;
    private String milkType;
    private String deliveryStatus;
}

