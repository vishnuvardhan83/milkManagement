package com.milkmanagement.service;

import com.milkmanagement.dto.DashboardStatsDTO;
import com.milkmanagement.entity.Customer;
import com.milkmanagement.entity.MilkDelivery;
import com.milkmanagement.entity.Payment;
import com.milkmanagement.repository.CustomerRepository;
import com.milkmanagement.repository.MilkDeliveryRepository;
import com.milkmanagement.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DashboardService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private MilkDeliveryRepository milkDeliveryRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardStats() {
        return getDashboardStats(null, null);
    }
    
    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardStats(java.time.LocalDate fromDate, java.time.LocalDate toDate) {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        long totalCustomers = customerRepository.count();
        long activeCustomers = customerRepository.findByDeliveryStatus(Customer.DeliveryStatus.ACTIVE).size();
        
        List<MilkDelivery> allDeliveries = milkDeliveryRepository.findAll();
        if (fromDate != null || toDate != null) {
            allDeliveries = allDeliveries.stream()
                    .filter(delivery -> {
                        java.time.LocalDate date = delivery.getDeliveryDate();
                        if (date == null) {
                            return false;
                        }
                        boolean afterFrom = fromDate == null || !date.isBefore(fromDate);
                        boolean beforeTo = toDate == null || !date.isAfter(toDate);
                        return afterFrom && beforeTo;
                    })
                    .toList();
        }
        
        BigDecimal totalRevenue = allDeliveries.stream()
                .map(MilkDelivery::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalDeliveries = allDeliveries.stream()
                .map(MilkDelivery::getQuantityDelivered)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        List<Payment> allPayments = paymentRepository.findAll();
        if (fromDate != null || toDate != null) {
            allPayments = allPayments.stream()
                    .filter(payment -> {
                        java.time.LocalDate date = payment.getPaymentDate();
                        if (date == null) {
                            return false;
                        }
                        boolean afterFrom = fromDate == null || !date.isBefore(fromDate);
                        boolean beforeTo = toDate == null || !date.isAfter(toDate);
                        return afterFrom && beforeTo;
                    })
                    .toList();
        }
        
        BigDecimal totalPayments = allPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal pendingAmount = totalRevenue.subtract(totalPayments);
        
        stats.setTotalCustomers(totalCustomers);
        stats.setActiveCustomers(activeCustomers);
        stats.setTotalRevenue(totalRevenue);
        stats.setTotalDeliveries(totalDeliveries);
        stats.setTotalPayments(totalPayments);
        stats.setPendingAmount(pendingAmount);
        stats.setTotalDeliveriesCount((long) allDeliveries.size());
        
        return stats;
    }
}
