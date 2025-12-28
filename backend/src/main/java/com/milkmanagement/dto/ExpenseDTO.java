package com.milkmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDTO {
    private Long id;
    private LocalDate expenseDate;
    private String category;
    private String description;
    private java.math.BigDecimal amount;
    private String paymentMethod;
    private String receiptNumber;
    private String notes;
}

