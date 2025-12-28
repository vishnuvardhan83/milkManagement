package com.milkmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyMilkCollectionDTO {
    private Long id;
    private Long animalId;
    private String animalTagNumber;
    private String animalName;
    private String animalType;
    private LocalDate collectionDate;
    private java.math.BigDecimal morningQuantity;
    private java.math.BigDecimal eveningQuantity;
    private java.math.BigDecimal totalQuantity;
    private String qualityGrade;
    private String notes;
}

