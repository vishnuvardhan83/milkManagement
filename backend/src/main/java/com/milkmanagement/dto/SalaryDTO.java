package com.milkmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private LocalDate salaryMonth;
    private java.math.BigDecimal baseSalary;
    private java.math.BigDecimal bonus;
    private java.math.BigDecimal deductions;
    private java.math.BigDecimal netSalary;
    private String paymentStatus;
    private LocalDate paymentDate;
    private String notes;
}

