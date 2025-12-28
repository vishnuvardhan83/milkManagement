package com.milkmanagement.service;

import com.milkmanagement.dto.ProfitLossDTO;
import com.milkmanagement.repository.DailyMilkCollectionRepository;
import com.milkmanagement.repository.ExpenseRepository;
import com.milkmanagement.repository.SalaryRepository;
import com.milkmanagement.repository.AnimalRepository;
import com.milkmanagement.repository.MilkDeliveryRepository;
import com.milkmanagement.repository.InventoryItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class ProfitLossService {
    
    @Autowired
    private DailyMilkCollectionRepository milkCollectionRepository;
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private SalaryRepository salaryRepository;
    
    @Autowired
    private AnimalRepository animalRepository;
    
    @Autowired
    private MilkDeliveryRepository milkDeliveryRepository;
    
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    
    @Transactional(readOnly = true)
    public ProfitLossDTO calculateProfitLoss(LocalDate startDate, LocalDate endDate) {
        // Make final copies for use in lambda expressions
        final LocalDate finalEndDate = (endDate == null) ? LocalDate.now() : endDate;
        final LocalDate finalStartDate = (startDate == null) ? finalEndDate.minusMonths(1) : startDate;
        
        // Calculate revenue from milk sales (deliveries)
        BigDecimal totalRevenue = milkDeliveryRepository.findAll().stream()
                .filter(delivery -> {
                    LocalDate deliveryDate = delivery.getDeliveryDate();
                    return !deliveryDate.isBefore(finalStartDate) && !deliveryDate.isAfter(finalEndDate);
                })
                .map(delivery -> delivery.getTotalAmount() != null ? delivery.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Total milk sales (same as revenue for now)
        BigDecimal totalMilkSales = totalRevenue;
        
        // Calculate expenses
        BigDecimal totalExpenses = expenseRepository.getTotalExpensesByDateRange(finalStartDate, finalEndDate);
        if (totalExpenses == null) {
            totalExpenses = BigDecimal.ZERO;
        }
        
        // Calculate salaries
        BigDecimal totalSalaries = salaryRepository.getTotalSalariesByDateRange(finalStartDate, finalEndDate);
        if (totalSalaries == null) {
            totalSalaries = BigDecimal.ZERO;
        }
        
        // Calculate inventory costs
        BigDecimal totalInventoryCost = inventoryItemRepository.getTotalInventoryValue();
        if (totalInventoryCost == null) {
            totalInventoryCost = BigDecimal.ZERO;
        }
        
        // Total costs (expenses + salaries + inventory cost)
        BigDecimal totalCosts = totalExpenses.add(totalSalaries).add(totalInventoryCost);
        
        // Net profit
        BigDecimal netProfit = totalRevenue.subtract(totalCosts);
        
        // Profit margin
        BigDecimal profitMargin = BigDecimal.ZERO;
        if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            profitMargin = netProfit.divide(totalRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        
        // Total animals
        Long totalAnimals = animalRepository.count();
        
        // Average daily milk
        BigDecimal totalMilk = milkCollectionRepository.getTotalMilkByDateRange(finalStartDate, finalEndDate);
        if (totalMilk == null) {
            totalMilk = BigDecimal.ZERO;
        }
        long days = ChronoUnit.DAYS.between(finalStartDate, finalEndDate) + 1;
        BigDecimal averageDailyMilk = days > 0 ? 
                totalMilk.divide(new BigDecimal(days), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        
        ProfitLossDTO dto = new ProfitLossDTO();
        dto.setStartDate(finalStartDate);
        dto.setEndDate(finalEndDate);
        dto.setTotalRevenue(totalRevenue);
        dto.setTotalMilkSales(totalMilkSales);
        dto.setTotalExpenses(totalExpenses);
        dto.setTotalSalaries(totalSalaries);
        dto.setTotalCosts(totalCosts);
        dto.setNetProfit(netProfit);
        dto.setProfitMargin(profitMargin);
        dto.setTotalAnimals(totalAnimals);
        dto.setAverageDailyMilk(averageDailyMilk);
        
        return dto;
    }
}

