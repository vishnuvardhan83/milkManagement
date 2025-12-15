package com.milkmanagement.dto;

import com.milkmanagement.entity.Payment;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotNull(message = "Payment date is required")
    private LocalDate paymentDate;
    
    private Payment.PaymentMethod paymentMethod;
    
    private String referenceNumber;
    
    private String notes;
    
    private Long receivedById;
    
    private String customerName;
    
    // Additional fields for frontend compatibility
    private Long invoiceId;
    private String status; // PENDING, PAID, PARTIAL
}
