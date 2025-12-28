package com.dairyfarm.service;

import com.dairyfarm.dto.DashboardStatsDTO;
import com.dairyfarm.entity.Animal;
import com.dairyfarm.repository.AnimalRepository;
import com.dairyfarm.repository.CustomerRepository;
import com.dairyfarm.repository.ExpenseRepository;
import com.dairyfarm.repository.InventoryItemRepository;
import com.dairyfarm.repository.MilkEntryRepository;
import com.dairyfarm.repository.ReceiptRepository;
import com.dairyfarm.repository.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class DashboardService {
    
    @Autowired
    private AnimalRepository animalRepository;
    
    @Autowired
    private MilkEntryRepository milkEntryRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private ReceiptRepository receiptRepository;
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private SalaryRepository salaryRepository;
    
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    
    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardStats(LocalDate fromDate, LocalDate toDate) {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        
        if (fromDate == null) {
            fromDate = startOfMonth;
        }
        if (toDate == null) {
            toDate = today;
        }
        
        Long totalAnimals = animalRepository.count();
        Long activeAnimals = animalRepository.countByStatus(Animal.AnimalStatus.ACTIVE);
        
        BigDecimal totalMilkToday = milkEntryRepository.getTotalMilkByDate(today);
        if (totalMilkToday == null) totalMilkToday = BigDecimal.ZERO;
        
        BigDecimal totalMilkThisMonth = milkEntryRepository.getTotalMilkByDateRange(startOfMonth, today);
        if (totalMilkThisMonth == null) totalMilkThisMonth = BigDecimal.ZERO;
        
        Long totalCustomers = customerRepository.count();
        Long activeCustomers = (long) customerRepository.findByDeliveryStatus(com.dairyfarm.entity.Customer.DeliveryStatus.ACTIVE).size();
        
        BigDecimal totalRevenueToday = receiptRepository.getTotalSalesByDateRange(today, today);
        if (totalRevenueToday == null) totalRevenueToday = BigDecimal.ZERO;
        
        BigDecimal totalRevenueThisMonth = receiptRepository.getTotalSalesByDateRange(startOfMonth, today);
        if (totalRevenueThisMonth == null) totalRevenueThisMonth = BigDecimal.ZERO;
        
        BigDecimal totalExpensesThisMonth = expenseRepository.getTotalExpensesByDateRange(startOfMonth, today);
        if (totalExpensesThisMonth == null) totalExpensesThisMonth = BigDecimal.ZERO;
        
        BigDecimal totalSalariesThisMonth = salaryRepository.getTotalSalariesByDateRange(startOfMonth, today);
        if (totalSalariesThisMonth == null) totalSalariesThisMonth = BigDecimal.ZERO;
        
        BigDecimal pendingPayments = receiptRepository.findAll().stream()
                .filter(r -> r.getPaymentStatus() != com.dairyfarm.entity.Receipt.PaymentStatus.PAID)
                .map(r -> r.getPendingAmount() != null ? r.getPendingAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Long pendingReceipts = (long) receiptRepository.findByPaymentStatus(com.dairyfarm.entity.Receipt.PaymentStatus.PENDING).size();
        
        Long lowStockItems = (long) inventoryItemRepository.findLowStockItems().size();
        
        DashboardStatsDTO stats = new DashboardStatsDTO();
        stats.setTotalAnimals(totalAnimals);
        stats.setActiveAnimals(activeAnimals);
        stats.setTotalMilkToday(totalMilkToday);
        stats.setTotalMilkThisMonth(totalMilkThisMonth);
        stats.setTotalCustomers(totalCustomers);
        stats.setActiveCustomers(activeCustomers);
        stats.setTotalRevenueToday(totalRevenueToday);
        stats.setTotalRevenueThisMonth(totalRevenueThisMonth);
        stats.setTotalExpensesThisMonth(totalExpensesThisMonth);
        stats.setTotalSalariesThisMonth(totalSalariesThisMonth);
        stats.setPendingPayments(pendingPayments);
        stats.setPendingReceipts(pendingReceipts);
        stats.setLowStockItems(lowStockItems);
        
        return stats;
    }
}

