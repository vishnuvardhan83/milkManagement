package com.dairyfarm.controller;

import com.dairyfarm.dto.DailyDeliveryLogDTO;
import com.dairyfarm.service.DeliveryService;
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
public class DeliveryController {
    
    @Autowired
    private DeliveryService deliveryService;
    
    private String getTenantId() {
        return "default";
    }
    
    @GetMapping
    public ResponseEntity<List<DailyDeliveryLogDTO>> getDeliveries(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            LocalDate deliveryDate = date != null ? date : LocalDate.now();
            List<DailyDeliveryLogDTO> deliveries = deliveryService.getDeliveriesForDate(getTenantId(), deliveryDate);
            return ResponseEntity.ok(deliveries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/delivery-boy/{deliveryBoyId}")
    public ResponseEntity<List<DailyDeliveryLogDTO>> getDeliveriesForDeliveryBoy(
            @PathVariable Long deliveryBoyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            LocalDate deliveryDate = date != null ? date : LocalDate.now();
            List<DailyDeliveryLogDTO> deliveries = deliveryService.getDeliveriesForDeliveryBoy(deliveryBoyId, deliveryDate);
            return ResponseEntity.ok(deliveries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/{id}/assign")
    public ResponseEntity<?> assignDeliveryBoy(
            @PathVariable Long id,
            @RequestParam Long deliveryBoyId) {
        try {
            DailyDeliveryLogDTO delivery = deliveryService.assignDeliveryBoy(id, deliveryBoyId);
            return ResponseEntity.ok(delivery);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/scan-qr")
    public ResponseEntity<?> scanQRCode(@RequestParam String qrCode) {
        try {
            DailyDeliveryLogDTO delivery = deliveryService.scanQRCode(qrCode);
            return ResponseEntity.ok(delivery);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/mark-delivered")
    public ResponseEntity<?> markDelivered(@PathVariable Long id) {
        try {
            DailyDeliveryLogDTO delivery = deliveryService.markDelivered(id);
            return ResponseEntity.ok(delivery);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/assign-tomorrow")
    public ResponseEntity<?> assignTomorrowDeliveries() {
        try {
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            deliveryService.assignDeliveriesForDate(getTenantId(), tomorrow);
            return ResponseEntity.ok("Deliveries assigned for tomorrow");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}

