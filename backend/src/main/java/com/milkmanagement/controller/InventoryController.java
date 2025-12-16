package com.milkmanagement.controller;

import com.milkmanagement.dto.InventoryEntryDTO;
import com.milkmanagement.dto.InventoryStatusDTO;
import com.milkmanagement.dto.InventoryUpdateRequest;
import com.milkmanagement.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/status")
    public ResponseEntity<InventoryStatusDTO> getInventoryStatus(@RequestParam(required = false) Long productId) {
        try {
            InventoryStatusDTO status = inventoryService.getInventoryStatus(productId);
            if (status == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateInventory(@Valid @RequestBody InventoryUpdateRequest request) {
        try {
            InventoryStatusDTO status = inventoryService.updateInventory(request);
            return ResponseEntity.ok(status);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/entries")
    public ResponseEntity<List<InventoryEntryDTO>> getAllInventoryEntries(
            @RequestParam(required = false) Long productId) {
        try {
            List<InventoryEntryDTO> entries = inventoryService.getAllInventoryEntries(productId);
            return ResponseEntity.ok(entries);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/entries/{id}")
    public ResponseEntity<InventoryEntryDTO> getInventoryEntryById(@PathVariable Long id) {
        try {
            InventoryEntryDTO entry = inventoryService.getInventoryEntryById(id);
            return ResponseEntity.ok(entry);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/entries/{id}")
    public ResponseEntity<?> updateInventoryEntry(@PathVariable Long id,
                                                  @Valid @RequestBody InventoryUpdateRequest request) {
        try {
            InventoryEntryDTO entry = inventoryService.updateInventoryEntry(id, request);
            return ResponseEntity.ok(entry);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/entries/{id}")
    public ResponseEntity<?> deleteInventoryEntry(@PathVariable Long id) {
        try {
            inventoryService.deleteInventoryEntry(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}
