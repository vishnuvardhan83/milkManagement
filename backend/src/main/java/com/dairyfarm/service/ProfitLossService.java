package com.dairyfarm.service;

import com.dairyfarm.dto.ProfitLossDTO;
import com.dairyfarm.repository.ExpenseRepository;
import com.dairyfarm.repository.InventoryItemRepository;
import com.dairyfarm.repository.MilkEntryRepository;
import com.dairyfarm.repository.ReceiptRepository;
import com.dairyfarm.repository.SalaryRepository;
import com.dairyfarm.repository.AnimalRepository;
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
    private MilkEntryRepository milkEntryRepository;
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private SalaryRepository salaryRepository;
    
    @Autowired
    private AnimalRepository animalRepository;
    
    @Autowired
    private ReceiptRepository receiptRepository;
    
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    
    @Transactional(readOnly = true)
    public ProfitLossDTO calculateProfitLoss(LocalDate startDate, LocalDate endDate) {
        final LocalDate finalEndDate = (endDate == null) ? LocalDate.now() : endDate;
        final LocalDate finalStartDate = (startDate == null) ? finalEndDate.minusMonths(1) : startDate;
        
        BigDecimal totalRevenue = receiptRepository.getTotalSalesByDateRange(finalStartDate, finalEndDate);
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }
        
        BigDecimal totalMilkSales = totalRevenue;
        
        BigDecimal totalExpenses = expenseRepository.getTotalExpensesByDateRange(finalStartDate, finalEndDate);
        if (totalExpenses == null) {
            totalExpenses = BigDecimal.ZERO;
        }
        
        BigDecimal totalSalaries = salaryRepository.getTotalSalariesByDateRange(finalStartDate, finalEndDate);
        if (totalSalaries == null) {
            totalSalaries = BigDecimal.ZERO;
        }
        
        BigDecimal totalInventoryCost = inventoryItemRepository.getTotalInventoryValue();
        if (totalInventoryCost == null) {
            totalInventoryCost = BigDecimal.ZERO;
        }
        
        BigDecimal totalCosts = totalExpenses.add(totalSalaries).add(totalInventoryCost);
        
        BigDecimal netProfit = totalRevenue.subtract(totalCosts);
        
        BigDecimal profitMargin = BigDecimal.ZERO;
        if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            profitMargin = netProfit.divide(totalRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        
        Long totalAnimals = animalRepository.count();
        
        BigDecimal totalMilk = milkEntryRepository.getTotalMilkByDateRange(finalStartDate, finalEndDate);
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
        dto.setTotalInventoryCost(totalInventoryCost);
        dto.setTotalCosts(totalCosts);
        dto.setNetProfit(netProfit);
        dto.setProfitMargin(profitMargin);
        dto.setTotalAnimals(totalAnimals);
        dto.setAverageDailyMilk(averageDailyMilk);
        
        return dto;
    }
}

