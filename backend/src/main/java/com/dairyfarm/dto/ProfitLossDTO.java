package com.dairyfarm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfitLossDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalRevenue;
    private BigDecimal totalMilkSales;
    private BigDecimal totalExpenses;
    private BigDecimal totalSalaries;
    private BigDecimal totalInventoryCost;
    private BigDecimal totalCosts;
    private BigDecimal netProfit;
    private BigDecimal profitMargin;
    private Long totalAnimals;
    private BigDecimal averageDailyMilk;
}

