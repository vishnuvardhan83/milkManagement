package com.dairyfarm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long receiptId;
    private String receiptNumber;
    private LocalDate paymentDate;
    private BigDecimal amount;
    private String paymentMethod;
    private String referenceNumber;
    private String status;
    private String notes;
}

