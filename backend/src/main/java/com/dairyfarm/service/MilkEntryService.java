package com.dairyfarm.service;

import com.dairyfarm.dto.MilkEntryDTO;
import com.dairyfarm.entity.Animal;
import com.dairyfarm.entity.MilkEntry;
import com.dairyfarm.entity.User;
import com.dairyfarm.repository.AnimalRepository;
import com.dairyfarm.repository.MilkEntryRepository;
import com.dairyfarm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MilkEntryService {
    
    @Autowired
    private MilkEntryRepository milkEntryRepository;
    
    @Autowired
    private AnimalRepository animalRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public List<MilkEntryDTO> getAllMilkEntries() {
        return milkEntryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MilkEntryDTO> getMilkEntriesByDate(LocalDate date) {
        return milkEntryRepository.findByEntryDate(date).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MilkEntryDTO> getMilkEntriesByDateRange(LocalDate startDate, LocalDate endDate) {
        return milkEntryRepository.findByEntryDateBetween(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public MilkEntryDTO getMilkEntryById(Long id) {
        MilkEntry entry = milkEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Milk entry not found with id: " + id));
        return convertToDTO(entry);
    }
    
    @Transactional
    public MilkEntryDTO createMilkEntry(MilkEntryDTO entryDTO) {
        Animal animal = animalRepository.findById(entryDTO.getAnimalId())
                .orElseThrow(() -> new RuntimeException("Animal not found with id: " + entryDTO.getAnimalId()));
        
        LocalDate entryDate = entryDTO.getEntryDate() != null ? entryDTO.getEntryDate() : LocalDate.now();
        
        milkEntryRepository.findByAnimalAndEntryDate(animal, entryDate)
                .ifPresent(existing -> {
                    throw new RuntimeException("Milk entry already exists for animal " + 
                            animal.getTagNumber() + " on " + entryDate);
                });
        
        MilkEntry entry = new MilkEntry();
        entry.setAnimal(animal);
        entry.setEntryDate(entryDate);
        entry.setMorningQuantity(entryDTO.getMorningQuantity());
        entry.setEveningQuantity(entryDTO.getEveningQuantity());
        entry.setQualityGrade(entryDTO.getQualityGrade());
        entry.setTemperature(entryDTO.getTemperature());
        entry.setNotes(entryDTO.getNotes());
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            entry.setRecordedBy(user);
        }
        
        MilkEntry saved = milkEntryRepository.save(entry);
        return convertToDTO(saved);
    }
    
    @Transactional
    public MilkEntryDTO updateMilkEntry(Long id, MilkEntryDTO entryDTO) {
        MilkEntry entry = milkEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Milk entry not found with id: " + id));
        
        entry.setMorningQuantity(entryDTO.getMorningQuantity());
        entry.setEveningQuantity(entryDTO.getEveningQuantity());
        entry.setQualityGrade(entryDTO.getQualityGrade());
        entry.setTemperature(entryDTO.getTemperature());
        entry.setNotes(entryDTO.getNotes());
        
        MilkEntry saved = milkEntryRepository.save(entry);
        return convertToDTO(saved);
    }
    
    @Transactional
    public void deleteMilkEntry(Long id) {
        if (!milkEntryRepository.existsById(id)) {
            throw new RuntimeException("Milk entry not found with id: " + id);
        }
        milkEntryRepository.deleteById(id);
    }
    
    private MilkEntryDTO convertToDTO(MilkEntry entry) {
        MilkEntryDTO dto = new MilkEntryDTO();
        dto.setId(entry.getId());
        dto.setAnimalId(entry.getAnimal().getId());
        dto.setAnimalTagNumber(entry.getAnimal().getTagNumber());
        dto.setAnimalName(entry.getAnimal().getName());
        dto.setAnimalType(entry.getAnimal().getAnimalType().name());
        dto.setEntryDate(entry.getEntryDate());
        dto.setMorningQuantity(entry.getMorningQuantity());
        dto.setEveningQuantity(entry.getEveningQuantity());
        dto.setTotalQuantity(entry.getTotalQuantity());
        dto.setQualityGrade(entry.getQualityGrade());
        dto.setTemperature(entry.getTemperature());
        dto.setNotes(entry.getNotes());
        return dto;
    }
}

