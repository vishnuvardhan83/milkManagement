package com.milkmanagement.controller;

import com.milkmanagement.dto.InventoryItemDTO;
import com.milkmanagement.dto.InventoryUsageDTO;
import com.milkmanagement.service.InventoryItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/inventory")
public class InventoryItemController {
    
    @Autowired
    private InventoryItemService inventoryItemService;
    
    @GetMapping
    public ResponseEntity<List<InventoryItemDTO>> getAllInventoryItems() {
        try {
            List<InventoryItemDTO> items = inventoryItemService.getAllInventoryItems();
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<InventoryItemDTO> getInventoryItemById(@PathVariable Long id) {
        try {
            InventoryItemDTO item = inventoryItemService.getInventoryItemById(id);
            return ResponseEntity.ok(item);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createInventoryItem(@Valid @RequestBody InventoryItemDTO itemDTO) {
        try {
            InventoryItemDTO created = inventoryItemService.createInventoryItem(itemDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateInventoryItem(@PathVariable Long id, 
                                                  @Valid @RequestBody InventoryItemDTO itemDTO) {
        try {
            InventoryItemDTO updated = inventoryItemService.updateInventoryItem(id, itemDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInventoryItem(@PathVariable Long id) {
        try {
            inventoryItemService.deleteInventoryItem(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/use")
    public ResponseEntity<?> useInventoryItem(@PathVariable Long id, 
                                               @Valid @RequestBody InventoryUsageDTO usageDTO) {
        try {
            InventoryUsageDTO usage = inventoryItemService.useInventoryItem(id, usageDTO);
            return ResponseEntity.ok(usage);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}/usage-history")
    public ResponseEntity<List<InventoryUsageDTO>> getUsageHistory(@PathVariable Long id) {
        try {
            List<InventoryUsageDTO> history = inventoryItemService.getUsageHistory(id);
            return ResponseEntity.ok(history);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

