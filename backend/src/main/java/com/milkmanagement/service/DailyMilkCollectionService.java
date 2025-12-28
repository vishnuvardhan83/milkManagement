package com.milkmanagement.service;

import com.milkmanagement.dto.DailyMilkCollectionDTO;
import com.milkmanagement.entity.Animal;
import com.milkmanagement.entity.DailyMilkCollection;
import com.milkmanagement.entity.User;
import com.milkmanagement.repository.AnimalRepository;
import com.milkmanagement.repository.DailyMilkCollectionRepository;
import com.milkmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DailyMilkCollectionService {
    
    @Autowired
    private DailyMilkCollectionRepository collectionRepository;
    
    @Autowired
    private AnimalRepository animalRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public List<DailyMilkCollectionDTO> getAllCollections() {
        return collectionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<DailyMilkCollectionDTO> getCollectionsByDate(LocalDate date) {
        return collectionRepository.findByCollectionDate(date).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<DailyMilkCollectionDTO> getCollectionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return collectionRepository.findByCollectionDateBetween(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public DailyMilkCollectionDTO getCollectionById(Long id) {
        DailyMilkCollection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Milk collection not found with id: " + id));
        return convertToDTO(collection);
    }
    
    @Transactional
    public DailyMilkCollectionDTO createCollection(DailyMilkCollectionDTO collectionDTO) {
        Animal animal = animalRepository.findById(collectionDTO.getAnimalId())
                .orElseThrow(() -> new RuntimeException("Animal not found with id: " + collectionDTO.getAnimalId()));
        
        LocalDate collectionDate = collectionDTO.getCollectionDate() != null ? 
                collectionDTO.getCollectionDate() : LocalDate.now();
        
        // Check if collection already exists for this animal and date
        collectionRepository.findByAnimalAndCollectionDate(animal, collectionDate)
                .ifPresent(existing -> {
                    throw new RuntimeException("Milk collection already exists for animal " + 
                            animal.getTagNumber() + " on " + collectionDate);
                });
        
        DailyMilkCollection collection = new DailyMilkCollection();
        collection.setAnimal(animal);
        collection.setCollectionDate(collectionDate);
        collection.setMorningQuantity(collectionDTO.getMorningQuantity());
        collection.setEveningQuantity(collectionDTO.getEveningQuantity());
        collection.setQualityGrade(collectionDTO.getQualityGrade());
        collection.setNotes(collectionDTO.getNotes());
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            collection.setCollectedBy(user);
        }
        
        DailyMilkCollection saved = collectionRepository.save(collection);
        return convertToDTO(saved);
    }
    
    @Transactional
    public DailyMilkCollectionDTO updateCollection(Long id, DailyMilkCollectionDTO collectionDTO) {
        DailyMilkCollection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Milk collection not found with id: " + id));
        
        collection.setMorningQuantity(collectionDTO.getMorningQuantity());
        collection.setEveningQuantity(collectionDTO.getEveningQuantity());
        collection.setQualityGrade(collectionDTO.getQualityGrade());
        collection.setNotes(collectionDTO.getNotes());
        
        DailyMilkCollection saved = collectionRepository.save(collection);
        return convertToDTO(saved);
    }
    
    @Transactional
    public void deleteCollection(Long id) {
        if (!collectionRepository.existsById(id)) {
            throw new RuntimeException("Milk collection not found with id: " + id);
        }
        collectionRepository.deleteById(id);
    }
    
    private DailyMilkCollectionDTO convertToDTO(DailyMilkCollection collection) {
        DailyMilkCollectionDTO dto = new DailyMilkCollectionDTO();
        dto.setId(collection.getId());
        dto.setAnimalId(collection.getAnimal().getId());
        dto.setAnimalTagNumber(collection.getAnimal().getTagNumber());
        dto.setAnimalName(collection.getAnimal().getName());
        dto.setAnimalType(collection.getAnimal().getAnimalType().name());
        dto.setCollectionDate(collection.getCollectionDate());
        dto.setMorningQuantity(collection.getMorningQuantity());
        dto.setEveningQuantity(collection.getEveningQuantity());
        dto.setTotalQuantity(collection.getTotalQuantity());
        dto.setQualityGrade(collection.getQualityGrade());
        dto.setNotes(collection.getNotes());
        return dto;
    }
}

