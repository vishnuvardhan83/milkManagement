package com.dairyfarm.controller;

import com.dairyfarm.dto.SubscriptionDTO;
import com.dairyfarm.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    
    @Autowired
    private SubscriptionService subscriptionService;
    
    private String getTenantId() {
        return "default";
    }
    
    @GetMapping
    public ResponseEntity<List<SubscriptionDTO>> getAllSubscriptions() {
        try {
            List<SubscriptionDTO> subscriptions = subscriptionService.getAllSubscriptions(getTenantId());
            return ResponseEntity.ok(subscriptions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionDTO> getSubscriptionById(@PathVariable Long id) {
        try {
            SubscriptionDTO subscription = subscriptionService.getSubscriptionById(id);
            return ResponseEntity.ok(subscription);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createSubscription(@Valid @RequestBody SubscriptionDTO dto) {
        try {
            SubscriptionDTO created = subscriptionService.createSubscription(dto, getTenantId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/pause")
    public ResponseEntity<?> pauseSubscription(@PathVariable Long id) {
        try {
            SubscriptionDTO subscription = subscriptionService.pauseSubscription(id);
            return ResponseEntity.ok(subscription);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/resume")
    public ResponseEntity<?> resumeSubscription(@PathVariable Long id) {
        try {
            SubscriptionDTO subscription = subscriptionService.resumeSubscription(id);
            return ResponseEntity.ok(subscription);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelSubscription(@PathVariable Long id) {
        try {
            SubscriptionDTO subscription = subscriptionService.cancelSubscription(id);
            return ResponseEntity.ok(subscription);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}

