package com.milkmanagement.controller;

import com.milkmanagement.dto.DailyMilkCollectionDTO;
import com.milkmanagement.service.DailyMilkCollectionService;
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
@RequestMapping("/api/milk-collections")
public class DailyMilkCollectionController {
    
    @Autowired
    private DailyMilkCollectionService collectionService;
    
    @GetMapping
    public ResponseEntity<List<DailyMilkCollectionDTO>> getAllCollections(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<DailyMilkCollectionDTO> collections;
            if (date != null) {
                collections = collectionService.getCollectionsByDate(date);
            } else if (startDate != null && endDate != null) {
                collections = collectionService.getCollectionsByDateRange(startDate, endDate);
            } else {
                collections = collectionService.getAllCollections();
            }
            return ResponseEntity.ok(collections);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DailyMilkCollectionDTO> getCollectionById(@PathVariable Long id) {
        try {
            DailyMilkCollectionDTO collection = collectionService.getCollectionById(id);
            return ResponseEntity.ok(collection);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createCollection(@Valid @RequestBody DailyMilkCollectionDTO collectionDTO) {
        try {
            DailyMilkCollectionDTO created = collectionService.createCollection(collectionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCollection(@PathVariable Long id, 
                                             @Valid @RequestBody DailyMilkCollectionDTO collectionDTO) {
        try {
            DailyMilkCollectionDTO updated = collectionService.updateCollection(id, collectionDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCollection(@PathVariable Long id) {
        try {
            collectionService.deleteCollection(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}

