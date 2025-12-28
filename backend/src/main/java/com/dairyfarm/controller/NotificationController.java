package com.dairyfarm.controller;

import com.dairyfarm.dto.NotificationRequestDTO;
import com.dairyfarm.entity.NotificationQueue;
import com.dairyfarm.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@Valid @RequestBody NotificationRequestDTO request) {
        try {
            NotificationQueue notification = notificationService.sendNotification(request);
            return ResponseEntity.ok(notification);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/send-email")
    public ResponseEntity<?> sendEmail(@RequestParam Long customerId,
                                       @RequestParam String subject,
                                       @RequestParam String message) {
        try {
            NotificationRequestDTO request = new NotificationRequestDTO();
            request.setCustomerId(customerId);
            request.setNotificationType("CUSTOM_MESSAGE");
            request.setChannel("EMAIL");
            request.setSubject(subject);
            request.setMessage(message);
            
            NotificationQueue notification = notificationService.sendNotification(request);
            return ResponseEntity.ok(notification);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/send-sms")
    public ResponseEntity<?> sendSMS(@RequestParam Long customerId,
                                     @RequestParam String message) {
        try {
            NotificationRequestDTO request = new NotificationRequestDTO();
            request.setCustomerId(customerId);
            request.setNotificationType("CUSTOM_MESSAGE");
            request.setChannel("SMS");
            request.setMessage(message);
            
            NotificationQueue notification = notificationService.sendNotification(request);
            return ResponseEntity.ok(notification);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/payment-reminder/{customerId}")
    public ResponseEntity<?> sendPaymentReminder(@PathVariable Long customerId) {
        try {
            notificationService.sendPaymentReminder(customerId);
            return ResponseEntity.ok("Payment reminder sent successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<NotificationQueue>> getPendingNotifications() {
        try {
            List<NotificationQueue> notifications = notificationService.getPendingNotifications();
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<NotificationQueue>> getNotificationsByCustomer(@PathVariable Long customerId) {
        try {
            List<NotificationQueue> notifications = notificationService.getNotificationsByCustomer(customerId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

