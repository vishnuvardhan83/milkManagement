package com.dairyfarm.service;

import com.dairyfarm.dto.SubscriptionDTO;
import com.dairyfarm.entity.Customer;
import com.dairyfarm.entity.Subscription;
import com.dairyfarm.entity.SubscriptionPlan;
import com.dairyfarm.entity.User;
import com.dairyfarm.repository.CustomerRepository;
import com.dairyfarm.repository.SubscriptionPlanRepository;
import com.dairyfarm.repository.SubscriptionRepository;
import com.dairyfarm.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {
    
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    
    @Autowired
    private SubscriptionPlanRepository planRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Transactional(readOnly = true)
    public List<SubscriptionDTO> getAllSubscriptions(String tenantId) {
        return subscriptionRepository.findByTenantId(tenantId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public SubscriptionDTO getSubscriptionById(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
        return convertToDTO(subscription);
    }
    
    @Transactional
    public SubscriptionDTO createSubscription(SubscriptionDTO dto, String tenantId) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        SubscriptionPlan plan = planRepository.findById(dto.getSubscriptionPlanId())
                .orElseThrow(() -> new RuntimeException("Subscription plan not found"));
        
        Subscription subscription = new Subscription();
        subscription.setTenantId(tenantId);
        subscription.setCustomer(customer);
        subscription.setSubscriptionPlan(plan);
        subscription.setStartDate(dto.getStartDate() != null ? dto.getStartDate() : LocalDate.now());
        subscription.setEndDate(subscription.getStartDate().plusDays(plan.getDurationDays()));
        subscription.setNextBillingDate(subscription.getStartDate().plusDays(30));
        subscription.setMonthlyAmount(plan.getMonthlyPrice());
        subscription.setStatus(Subscription.SubscriptionStatus.ACTIVE);
        subscription.setAutoRenew(dto.getAutoRenew() != null ? dto.getAutoRenew() : true);
        subscription.setProductsJson(dto.getProductsJson());
        subscription.setWalletBalance(customer.getWalletBalance());
        subscription.setLoyaltyPoints(customer.getLoyaltyPoints());
        
        if (dto.getCouponCode() != null) {
            subscription.setCouponCode(dto.getCouponCode());
            subscription.setDiscountAmount(calculateDiscount(plan.getMonthlyPrice(), dto.getCouponCode()));
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            subscription.setCreatedBy(user);
        }
        
        Subscription saved = subscriptionRepository.save(subscription);
        return convertToDTO(saved);
    }
    
    @Transactional
    public SubscriptionDTO pauseSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
        
        subscription.setPaused(true);
        subscription.setPauseStartDate(LocalDate.now());
        subscription.setStatus(Subscription.SubscriptionStatus.PAUSED);
        
        Subscription saved = subscriptionRepository.save(subscription);
        return convertToDTO(saved);
    }
    
    @Transactional
    public SubscriptionDTO resumeSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
        
        subscription.setPaused(false);
        subscription.setPauseEndDate(LocalDate.now());
        subscription.setStatus(Subscription.SubscriptionStatus.ACTIVE);
        
        if (subscription.getPauseStartDate() != null) {
            long pausedDays = java.time.temporal.ChronoUnit.DAYS.between(
                    subscription.getPauseStartDate(), LocalDate.now());
            subscription.setEndDate(subscription.getEndDate().plusDays(pausedDays));
        }
        
        Subscription saved = subscriptionRepository.save(subscription);
        return convertToDTO(saved);
    }
    
    @Transactional
    public SubscriptionDTO cancelSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
        
        subscription.setStatus(Subscription.SubscriptionStatus.CANCELLED);
        subscription.setAutoRenew(false);
        
        Subscription saved = subscriptionRepository.save(subscription);
        return convertToDTO(saved);
    }
    
    @Transactional
    public void renewSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
        
        if (!subscription.getAutoRenew() || subscription.getStatus() != Subscription.SubscriptionStatus.ACTIVE) {
            return;
        }
        
        BigDecimal amount = subscription.getMonthlyAmount();
        BigDecimal walletBalance = subscription.getWalletBalance();
        
        if (walletBalance.compareTo(amount) >= 0) {
            subscription.setWalletBalance(walletBalance.subtract(amount));
            subscription.setNextBillingDate(subscription.getNextBillingDate().plusDays(30));
            subscription.setEndDate(subscription.getEndDate().plusDays(30));
            
            subscription.getCustomer().setWalletBalance(subscription.getWalletBalance());
            customerRepository.save(subscription.getCustomer());
        } else {
            subscription.setStatus(Subscription.SubscriptionStatus.PENDING_PAYMENT);
        }
        
        subscriptionRepository.save(subscription);
    }
    
    private BigDecimal calculateDiscount(BigDecimal amount, String couponCode) {
        return BigDecimal.ZERO;
    }
    
    private SubscriptionDTO convertToDTO(Subscription subscription) {
        SubscriptionDTO dto = new SubscriptionDTO();
        dto.setId(subscription.getId());
        dto.setSubscriptionNumber(subscription.getSubscriptionNumber());
        dto.setCustomerId(subscription.getCustomer().getId());
        dto.setCustomerName(subscription.getCustomer().getName());
        dto.setSubscriptionPlanId(subscription.getSubscriptionPlan().getId());
        dto.setPlanName(subscription.getSubscriptionPlan().getName());
        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setNextBillingDate(subscription.getNextBillingDate());
        dto.setMonthlyAmount(subscription.getMonthlyAmount());
        dto.setStatus(subscription.getStatus().name());
        dto.setAutoRenew(subscription.getAutoRenew());
        dto.setPaused(subscription.getPaused());
        dto.setWalletBalance(subscription.getWalletBalance());
        dto.setLoyaltyPoints(subscription.getLoyaltyPoints());
        dto.setCouponCode(subscription.getCouponCode());
        dto.setDiscountAmount(subscription.getDiscountAmount());
        dto.setProductsJson(subscription.getProductsJson());
        return dto;
    }
}

