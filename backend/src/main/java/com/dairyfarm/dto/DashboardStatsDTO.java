package com.dairyfarm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private Long totalAnimals;
    private Long activeAnimals;
    private BigDecimal totalMilkToday;
    private BigDecimal totalMilkThisMonth;
    private Long totalCustomers;
    private Long activeCustomers;
    private BigDecimal totalRevenueToday;
    private BigDecimal totalRevenueThisMonth;
    private BigDecimal totalExpensesThisMonth;
    private BigDecimal totalSalariesThisMonth;
    private BigDecimal pendingPayments;
    private Long pendingReceipts;
    private Long lowStockItems;
}

