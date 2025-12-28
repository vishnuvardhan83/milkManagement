package com.dairyfarm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MilkEntryDTO {
    private Long id;
    private Long animalId;
    private String animalTagNumber;
    private String animalName;
    private String animalType;
    private LocalDate entryDate;
    private BigDecimal morningQuantity;
    private BigDecimal eveningQuantity;
    private BigDecimal totalQuantity;
    private String qualityGrade;
    private BigDecimal temperature;
    private String notes;
}

