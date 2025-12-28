package com.dairyfarm.controller;

import com.dairyfarm.dto.SalaryDTO;
import com.dairyfarm.service.SalaryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/salaries")
public class SalaryController {
    
    @Autowired
    private SalaryService salaryService;
    
    @GetMapping
    public ResponseEntity<List<SalaryDTO>> getAllSalaries() {
        try {
            List<SalaryDTO> salaries = salaryService.getAllSalaries();
            return ResponseEntity.ok(salaries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SalaryDTO> getSalaryById(@PathVariable Long id) {
        try {
            SalaryDTO salary = salaryService.getSalaryById(id);
            return ResponseEntity.ok(salary);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createSalary(@Valid @RequestBody SalaryDTO salaryDTO) {
        try {
            SalaryDTO created = salaryService.createSalary(salaryDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSalary(@PathVariable Long id, 
                                          @Valid @RequestBody SalaryDTO salaryDTO) {
        try {
            SalaryDTO updated = salaryService.updateSalary(id, salaryDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSalary(@PathVariable Long id) {
        try {
            salaryService.deleteSalary(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}

