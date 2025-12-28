package com.dairyfarm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDTO {
    private Long id;
    private String subscriptionNumber;
    private Long customerId;
    private String customerName;
    private Long subscriptionPlanId;
    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextBillingDate;
    private BigDecimal monthlyAmount;
    private String status;
    private Boolean autoRenew;
    private Boolean paused;
    private BigDecimal walletBalance;
    private Integer loyaltyPoints;
    private String couponCode;
    private BigDecimal discountAmount;
    private String productsJson;
}

