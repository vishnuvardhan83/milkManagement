package com.dairyfarm.scheduler;

import com.dairyfarm.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DeliveryScheduler {
    
    @Autowired
    private DeliveryService deliveryService;
    
    @Scheduled(cron = "0 0 20 * * ?")
    public void assignNextDayDeliveries() {
        String defaultTenantId = "default";
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        deliveryService.assignDeliveriesForDate(defaultTenantId, tomorrow);
    }
}

