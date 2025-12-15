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
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        long totalCustomers = customerRepository.count();
        long activeCustomers = customerRepository.findByDeliveryStatus(Customer.DeliveryStatus.ACTIVE).size();
        
        List<MilkDelivery> allDeliveries = milkDeliveryRepository.findAll();
        BigDecimal totalRevenue = allDeliveries.stream()
                .map(MilkDelivery::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalDeliveries = allDeliveries.stream()
                .map(MilkDelivery::getQuantityDelivered)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        List<Payment> allPayments = paymentRepository.findAll();
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
