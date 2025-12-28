package com.dairyfarm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnimalDTO {
    private Long id;
    private String tagNumber;
    private String name;
    private String animalType;
    private String breed;
    private LocalDate dateOfBirth;
    private LocalDate purchaseDate;
    private BigDecimal purchasePrice;
    private String status;
    private String healthStatus;
    private String notes;
}

