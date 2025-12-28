package com.dairyfarm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyDeliveryLogDTO {
    private Long id;
    private Long subscriptionId;
    private String subscriptionNumber;
    private Long customerId;
    private String customerName;
    private String customerAddress;
    private String customerMobile;
    private Long deliveryBoyId;
    private String deliveryBoyName;
    private LocalDate deliveryDate;
    private String scheduledTime;
    private LocalDateTime actualDeliveryTime;
    private String productsDeliveredJson;
    private BigDecimal quantityDelivered;
    private String deliveryStatus;
    private String qrCode;
    private Boolean qrScanned;
    private LocalDateTime qrScanTime;
    private String deliveryNotes;
}

