package com.dairyfarm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDTO {
    private Long id;
    private String receiptNumber;
    private Long customerId;
    private String customerName;
    private String customerMobile;
    private LocalDate receiptDate;
    private BigDecimal quantityLiters;
    private BigDecimal milkRate;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private BigDecimal paidAmount;
    private BigDecimal pendingAmount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String notes;
}

