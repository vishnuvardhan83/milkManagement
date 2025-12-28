package com.dairyfarm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDTO {
    private Long id;
    private LocalDate expenseDate;
    private String category;
    private String description;
    private BigDecimal amount;
    private String paymentMethod;
    private String receiptNumber;
    private String notes;
}

