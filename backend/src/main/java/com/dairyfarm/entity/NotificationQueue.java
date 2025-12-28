package com.dairyfarm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_queue")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationQueue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 50)
    private NotificationType notificationType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private Channel channel;
    
    @Column(name = "recipient", nullable = false, length = 255)
    private String recipient;
    
    @Column(name = "subject", length = 255)
    private String subject;
    
    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private NotificationStatus status;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = NotificationStatus.PENDING;
        }
    }
    
    public enum NotificationType {
        PAYMENT_REMINDER, RECEIPT_GENERATED, BALANCE_UPDATE, CUSTOM_MESSAGE
    }
    
    public enum Channel {
        EMAIL, SMS, BOTH
    }
    
    public enum NotificationStatus {
        PENDING, SENT, FAILED, CANCELLED
    }
}

