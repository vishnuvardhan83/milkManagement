package com.milkmanagement.service;

import com.milkmanagement.dto.NotificationRequestDTO;
import com.milkmanagement.entity.Customer;
import com.milkmanagement.entity.NotificationQueue;
import com.milkmanagement.repository.CustomerRepository;
import com.milkmanagement.repository.NotificationQueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationQueueRepository notificationQueueRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Transactional
    public NotificationQueue sendNotification(NotificationRequestDTO request) {
        Customer customer = null;
        if (request.getCustomerId() != null) {
            customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
        }
        
        NotificationQueue notification = new NotificationQueue();
        notification.setCustomer(customer);
        notification.setNotificationType(NotificationQueue.NotificationType.valueOf(request.getNotificationType()));
        notification.setChannel(NotificationQueue.Channel.valueOf(request.getChannel()));
        notification.setSubject(request.getSubject());
        notification.setMessage(request.getMessage());
        notification.setStatus(NotificationQueue.NotificationStatus.PENDING);
        
        if (customer != null) {
            if (request.getChannel().equals("EMAIL") || request.getChannel().equals("BOTH")) {
                notification.setRecipient(customer.getEmail());
            } else {
                notification.setRecipient(customer.getMobileNumber());
            }
        }
        
        NotificationQueue saved = notificationQueueRepository.save(notification);
        
        try {
            processNotification(saved);
        } catch (Exception e) {
            saved.setStatus(NotificationQueue.NotificationStatus.FAILED);
            saved.setErrorMessage(e.getMessage());
            notificationQueueRepository.save(saved);
        }
        
        return saved;
    }
    
    @Transactional
    public void processNotification(NotificationQueue notification) {
        if (notification.getChannel() == NotificationQueue.Channel.EMAIL || 
            notification.getChannel() == NotificationQueue.Channel.BOTH) {
            sendEmail(notification);
        }
        
        if (notification.getChannel() == NotificationQueue.Channel.SMS || 
            notification.getChannel() == NotificationQueue.Channel.BOTH) {
            sendSMS(notification);
        }
        
        notification.setStatus(NotificationQueue.NotificationStatus.SENT);
        notification.setSentAt(java.time.LocalDateTime.now());
        notificationQueueRepository.save(notification);
    }
    
    private void sendEmail(NotificationQueue notification) {
        if (mailSender == null) {
            throw new RuntimeException("Email service not configured");
        }
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notification.getRecipient());
        message.setSubject(notification.getSubject() != null ? notification.getSubject() : "Dairy Farm Notification");
        message.setText(notification.getMessage());
        mailSender.send(message);
    }
    
    private void sendSMS(NotificationQueue notification) {
        // SMS implementation using Twilio or other provider
        // This is a placeholder - implement based on your SMS provider
        throw new RuntimeException("SMS service not implemented. Configure Twilio credentials in application.properties");
    }
    
    @Transactional(readOnly = true)
    public List<NotificationQueue> getPendingNotifications() {
        return notificationQueueRepository.findByStatusOrderByCreatedAtAsc(NotificationQueue.NotificationStatus.PENDING);
    }
    
    @Transactional
    public void sendPaymentReminder(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setCustomerId(customerId);
        request.setNotificationType("PAYMENT_REMINDER");
        request.setChannel("EMAIL");
        request.setSubject("Payment Reminder - Dairy Farm");
        request.setMessage("Dear " + customer.getName() + ",\n\n" +
                "You have a pending balance. Please clear your dues at your earliest convenience.\n\n" +
                "Thank you!");
        
        sendNotification(request);
    }
}

