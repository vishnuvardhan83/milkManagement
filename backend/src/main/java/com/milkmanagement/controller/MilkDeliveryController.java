package com.milkmanagement.controller;

import com.milkmanagement.dto.MilkDeliveryDTO;
import com.milkmanagement.service.MilkDeliveryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/deliveries")
public class MilkDeliveryController {
    
    @Autowired
    private MilkDeliveryService milkDeliveryService;
    
    @GetMapping
    public ResponseEntity<List<MilkDeliveryDTO>> getAllDeliveries() {
        try {
            List<MilkDeliveryDTO> deliveries = milkDeliveryService.getAllDeliveries();
            return ResponseEntity.ok(deliveries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<MilkDeliveryDTO>> getDeliveriesByCustomer(@PathVariable Long customerId) {
        try {
            List<MilkDeliveryDTO> deliveries = milkDeliveryService.getDeliveriesByCustomer(customerId);
            return ResponseEntity.ok(deliveries);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/date/{date}")
    public ResponseEntity<List<MilkDeliveryDTO>> getDeliveriesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<MilkDeliveryDTO> deliveries = milkDeliveryService.getDeliveriesByDate(date);
            return ResponseEntity.ok(deliveries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createDelivery(@Valid @RequestBody MilkDeliveryDTO deliveryDTO) {
        try {
            MilkDeliveryDTO createdDelivery = milkDeliveryService.createDelivery(deliveryDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDelivery);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}
