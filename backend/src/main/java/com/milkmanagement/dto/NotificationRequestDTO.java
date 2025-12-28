package com.milkmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDTO {
    private Long customerId;
    private String notificationType;
    private String channel;
    private String subject;
    private String message;
}

