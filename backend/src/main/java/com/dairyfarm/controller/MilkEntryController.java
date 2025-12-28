package com.dairyfarm.controller;

import com.dairyfarm.dto.MilkEntryDTO;
import com.dairyfarm.service.MilkEntryService;
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
@RequestMapping("/api/milk-entries")
public class MilkEntryController {
    
    @Autowired
    private MilkEntryService milkEntryService;
    
    @GetMapping
    public ResponseEntity<List<MilkEntryDTO>> getAllMilkEntries(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<MilkEntryDTO> entries;
            if (date != null) {
                entries = milkEntryService.getMilkEntriesByDate(date);
            } else if (startDate != null && endDate != null) {
                entries = milkEntryService.getMilkEntriesByDateRange(startDate, endDate);
            } else {
                entries = milkEntryService.getAllMilkEntries();
            }
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MilkEntryDTO> getMilkEntryById(@PathVariable Long id) {
        try {
            MilkEntryDTO entry = milkEntryService.getMilkEntryById(id);
            return ResponseEntity.ok(entry);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createMilkEntry(@Valid @RequestBody MilkEntryDTO entryDTO) {
        try {
            MilkEntryDTO created = milkEntryService.createMilkEntry(entryDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMilkEntry(@PathVariable Long id, 
                                             @Valid @RequestBody MilkEntryDTO entryDTO) {
        try {
            MilkEntryDTO updated = milkEntryService.updateMilkEntry(id, entryDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMilkEntry(@PathVariable Long id) {
        try {
            milkEntryService.deleteMilkEntry(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}

