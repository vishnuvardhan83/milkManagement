package com.milkmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private Long totalCustomers;
    private Long activeCustomers;
    private BigDecimal totalRevenue;
    private BigDecimal totalDeliveries;
    private BigDecimal totalPayments;
    private BigDecimal pendingAmount;
    private Long totalDeliveriesCount;
}
