package com.milkmanagement.service;

import com.milkmanagement.dto.AnimalDTO;
import com.milkmanagement.entity.Animal;
import com.milkmanagement.entity.User;
import com.milkmanagement.repository.AnimalRepository;
import com.milkmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnimalService {
    
    @Autowired
    private AnimalRepository animalRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public List<AnimalDTO> getAllAnimals() {
        return animalRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public AnimalDTO getAnimalById(Long id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Animal not found with id: " + id));
        return convertToDTO(animal);
    }
    
    @Transactional
    public AnimalDTO createAnimal(AnimalDTO animalDTO) {
        if (animalRepository.existsByTagNumber(animalDTO.getTagNumber())) {
            throw new RuntimeException("Animal with tag number " + animalDTO.getTagNumber() + " already exists");
        }
        
        Animal animal = new Animal();
        animal.setTagNumber(animalDTO.getTagNumber());
        animal.setName(animalDTO.getName());
        animal.setAnimalType(Animal.AnimalType.valueOf(animalDTO.getAnimalType()));
        if (animalDTO.getBreed() != null) {
            animal.setBreed(Animal.Breed.valueOf(animalDTO.getBreed()));
        }
        animal.setDateOfBirth(animalDTO.getDateOfBirth());
        animal.setPurchaseDate(animalDTO.getPurchaseDate());
        animal.setPurchasePrice(animalDTO.getPurchasePrice());
        if (animalDTO.getStatus() != null) {
            animal.setStatus(Animal.AnimalStatus.valueOf(animalDTO.getStatus()));
        }
        animal.setHealthStatus(animalDTO.getHealthStatus());
        animal.setNotes(animalDTO.getNotes());
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            animal.setCreatedBy(user);
        }
        
        Animal saved = animalRepository.save(animal);
        return convertToDTO(saved);
    }
    
    @Transactional
    public AnimalDTO updateAnimal(Long id, AnimalDTO animalDTO) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Animal not found with id: " + id));
        
        if (!animal.getTagNumber().equals(animalDTO.getTagNumber()) && 
            animalRepository.existsByTagNumber(animalDTO.getTagNumber())) {
            throw new RuntimeException("Animal with tag number " + animalDTO.getTagNumber() + " already exists");
        }
        
        animal.setTagNumber(animalDTO.getTagNumber());
        animal.setName(animalDTO.getName());
        animal.setAnimalType(Animal.AnimalType.valueOf(animalDTO.getAnimalType()));
        if (animalDTO.getBreed() != null) {
            animal.setBreed(Animal.Breed.valueOf(animalDTO.getBreed()));
        }
        animal.setDateOfBirth(animalDTO.getDateOfBirth());
        animal.setPurchaseDate(animalDTO.getPurchaseDate());
        animal.setPurchasePrice(animalDTO.getPurchasePrice());
        if (animalDTO.getStatus() != null) {
            animal.setStatus(Animal.AnimalStatus.valueOf(animalDTO.getStatus()));
        }
        animal.setHealthStatus(animalDTO.getHealthStatus());
        animal.setNotes(animalDTO.getNotes());
        
        Animal saved = animalRepository.save(animal);
        return convertToDTO(saved);
    }
    
    @Transactional
    public void deleteAnimal(Long id) {
        if (!animalRepository.existsById(id)) {
            throw new RuntimeException("Animal not found with id: " + id);
        }
        animalRepository.deleteById(id);
    }
    
    private AnimalDTO convertToDTO(Animal animal) {
        AnimalDTO dto = new AnimalDTO();
        dto.setId(animal.getId());
        dto.setTagNumber(animal.getTagNumber());
        dto.setName(animal.getName());
        dto.setAnimalType(animal.getAnimalType().name());
        if (animal.getBreed() != null) {
            dto.setBreed(animal.getBreed().name());
        }
        dto.setDateOfBirth(animal.getDateOfBirth());
        dto.setPurchaseDate(animal.getPurchaseDate());
        dto.setPurchasePrice(animal.getPurchasePrice());
        dto.setStatus(animal.getStatus().name());
        dto.setHealthStatus(animal.getHealthStatus());
        dto.setNotes(animal.getNotes());
        return dto;
    }
}

