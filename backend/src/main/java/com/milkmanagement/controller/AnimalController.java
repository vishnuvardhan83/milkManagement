package com.milkmanagement.controller;

import com.milkmanagement.dto.AnimalDTO;
import com.milkmanagement.service.AnimalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/animals")
public class AnimalController {
    
    @Autowired
    private AnimalService animalService;
    
    @GetMapping
    public ResponseEntity<List<AnimalDTO>> getAllAnimals() {
        try {
            List<AnimalDTO> animals = animalService.getAllAnimals();
            return ResponseEntity.ok(animals);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AnimalDTO> getAnimalById(@PathVariable Long id) {
        try {
            AnimalDTO animal = animalService.getAnimalById(id);
            return ResponseEntity.ok(animal);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createAnimal(@Valid @RequestBody AnimalDTO animalDTO) {
        try {
            AnimalDTO created = animalService.createAnimal(animalDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAnimal(@PathVariable Long id, 
                                         @Valid @RequestBody AnimalDTO animalDTO) {
        try {
            AnimalDTO updated = animalService.updateAnimal(id, animalDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAnimal(@PathVariable Long id) {
        try {
            animalService.deleteAnimal(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}

