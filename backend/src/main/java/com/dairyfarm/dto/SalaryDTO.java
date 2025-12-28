package com.dairyfarm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private LocalDate salaryMonth;
    private BigDecimal baseSalary;
    private BigDecimal bonus;
    private BigDecimal deductions;
    private BigDecimal netSalary;
    private String paymentStatus;
    private LocalDate paymentDate;
    private String notes;
}

