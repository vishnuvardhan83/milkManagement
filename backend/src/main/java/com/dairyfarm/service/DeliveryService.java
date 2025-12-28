package com.dairyfarm.service;

import com.dairyfarm.dto.DailyDeliveryLogDTO;
import com.dairyfarm.entity.Customer;
import com.dairyfarm.entity.DailyDeliveryLog;
import com.dairyfarm.entity.Subscription;
import com.dairyfarm.entity.User;
import com.dairyfarm.repository.CustomerRepository;
import com.dairyfarm.repository.DailyDeliveryLogRepository;
import com.dairyfarm.repository.SubscriptionRepository;
import com.dairyfarm.repository.UserRepository;
import com.dairyfarm.util.QRCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeliveryService {
    
    @Autowired
    private DailyDeliveryLogRepository deliveryLogRepository;
    
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private QRCodeGenerator qrCodeGenerator;
    
    @Transactional
    public void assignDeliveriesForDate(String tenantId, LocalDate date) {
        List<Subscription> activeSubscriptions = subscriptionRepository.findActiveSubscriptions(tenantId);
        
        for (Subscription subscription : activeSubscriptions) {
            if (subscription.getPaused()) {
                continue;
            }
            
            DailyDeliveryLog existing = deliveryLogRepository.findByTenantIdAndDeliveryDate(tenantId, date)
                    .stream()
                    .filter(d -> d.getSubscription().getId().equals(subscription.getId()))
                    .findFirst()
                    .orElse(null);
            
            if (existing == null) {
                DailyDeliveryLog deliveryLog = new DailyDeliveryLog();
                deliveryLog.setTenantId(tenantId);
                deliveryLog.setSubscription(subscription);
                deliveryLog.setCustomer(subscription.getCustomer());
                deliveryLog.setDeliveryDate(date);
                deliveryLog.setDeliveryStatus(DailyDeliveryLog.DeliveryStatus.PENDING);
                deliveryLog.setProductsDeliveredJson(subscription.getProductsJson());
                deliveryLog.setQrCode(qrCodeGenerator.generateQRCodeForDelivery(
                        null, subscription.getSubscriptionNumber()));
                
                deliveryLogRepository.save(deliveryLog);
            }
        }
    }
    
    @Transactional(readOnly = true)
    public List<DailyDeliveryLogDTO> getDeliveriesForDate(String tenantId, LocalDate date) {
        return deliveryLogRepository.findByTenantIdAndDeliveryDate(tenantId, date).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<DailyDeliveryLogDTO> getDeliveriesForDeliveryBoy(Long deliveryBoyId, LocalDate date) {
        User deliveryBoy = userRepository.findById(deliveryBoyId)
                .orElseThrow(() -> new RuntimeException("Delivery boy not found"));
        
        return deliveryLogRepository.findByDeliveryBoyAndDeliveryDate(deliveryBoy, date).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public DailyDeliveryLogDTO assignDeliveryBoy(Long deliveryLogId, Long deliveryBoyId) {
        DailyDeliveryLog deliveryLog = deliveryLogRepository.findById(deliveryLogId)
                .orElseThrow(() -> new RuntimeException("Delivery log not found"));
        
        User deliveryBoy = userRepository.findById(deliveryBoyId)
                .orElseThrow(() -> new RuntimeException("Delivery boy not found"));
        
        deliveryLog.setDeliveryBoy(deliveryBoy);
        deliveryLog.setDeliveryStatus(DailyDeliveryLog.DeliveryStatus.ASSIGNED);
        
        DailyDeliveryLog saved = deliveryLogRepository.save(deliveryLog);
        return convertToDTO(saved);
    }
    
    @Transactional
    public DailyDeliveryLogDTO scanQRCode(String qrCode) {
        DailyDeliveryLog deliveryLog = deliveryLogRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new RuntimeException("Invalid QR code"));
        
        deliveryLog.setQrScanned(true);
        deliveryLog.setQrScanTime(LocalDateTime.now());
        deliveryLog.setDeliveryStatus(DailyDeliveryLog.DeliveryStatus.DELIVERED);
        deliveryLog.setActualDeliveryTime(LocalDateTime.now());
        
        DailyDeliveryLog saved = deliveryLogRepository.save(deliveryLog);
        return convertToDTO(saved);
    }
    
    @Transactional
    public DailyDeliveryLogDTO markDelivered(Long deliveryLogId) {
        DailyDeliveryLog deliveryLog = deliveryLogRepository.findById(deliveryLogId)
                .orElseThrow(() -> new RuntimeException("Delivery log not found"));
        
        deliveryLog.setDeliveryStatus(DailyDeliveryLog.DeliveryStatus.DELIVERED);
        deliveryLog.setActualDeliveryTime(LocalDateTime.now());
        
        Subscription subscription = deliveryLog.getSubscription();
        subscription.setEndDate(subscription.getEndDate().plusDays(1));
        subscriptionRepository.save(subscription);
        
        DailyDeliveryLog saved = deliveryLogRepository.save(deliveryLog);
        return convertToDTO(saved);
    }
    
    private DailyDeliveryLogDTO convertToDTO(DailyDeliveryLog log) {
        DailyDeliveryLogDTO dto = new DailyDeliveryLogDTO();
        dto.setId(log.getId());
        dto.setSubscriptionId(log.getSubscription().getId());
        dto.setSubscriptionNumber(log.getSubscription().getSubscriptionNumber());
        dto.setCustomerId(log.getCustomer().getId());
        dto.setCustomerName(log.getCustomer().getName());
        dto.setCustomerAddress(log.getCustomer().getAddress());
        dto.setCustomerMobile(log.getCustomer().getMobileNumber());
        if (log.getDeliveryBoy() != null) {
            dto.setDeliveryBoyId(log.getDeliveryBoy().getId());
            dto.setDeliveryBoyName(log.getDeliveryBoy().getFullName());
        }
        dto.setDeliveryDate(log.getDeliveryDate());
        dto.setScheduledTime(log.getScheduledTime());
        dto.setActualDeliveryTime(log.getActualDeliveryTime());
        dto.setProductsDeliveredJson(log.getProductsDeliveredJson());
        dto.setQuantityDelivered(log.getQuantityDelivered());
        dto.setDeliveryStatus(log.getDeliveryStatus().name());
        dto.setQrCode(log.getQrCode());
        dto.setQrScanned(log.getQrScanned());
        dto.setQrScanTime(log.getQrScanTime());
        dto.setDeliveryNotes(log.getDeliveryNotes());
        return dto;
    }
}

