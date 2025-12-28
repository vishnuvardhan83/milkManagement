package com.dairyfarm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBalanceDTO {
    private Long customerId;
    private String customerName;
    private String mobileNumber;
    private String email;
    private BigDecimal totalPendingAmount;
    private Long pendingReceiptsCount;
}

