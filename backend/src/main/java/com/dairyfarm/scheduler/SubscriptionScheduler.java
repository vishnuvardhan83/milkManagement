package com.dairyfarm.scheduler;

import com.dairyfarm.repository.SubscriptionRepository;
import com.dairyfarm.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SubscriptionScheduler {
    
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    
    @Autowired
    private SubscriptionService subscriptionService;
    
    @Scheduled(cron = "0 0 1 * * ?")
    public void renewMonthlySubscriptions() {
        String defaultTenantId = "default";
        LocalDate today = LocalDate.now();
        
        subscriptionRepository.findSubscriptionsDueForRenewal(defaultTenantId, today)
                .forEach(subscription -> {
                    try {
                        subscriptionService.renewSubscription(subscription.getId());
                    } catch (Exception e) {
                        System.err.println("Failed to renew subscription " + subscription.getId() + ": " + e.getMessage());
                    }
                });
    }
}

