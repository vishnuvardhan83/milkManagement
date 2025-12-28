package com.dairyfarm.repository;

import com.dairyfarm.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {
    Optional<Animal> findByTagNumber(String tagNumber);
    List<Animal> findByAnimalType(Animal.AnimalType animalType);
    List<Animal> findByStatus(Animal.AnimalStatus status);
    boolean existsByTagNumber(String tagNumber);
    long countByStatus(Animal.AnimalStatus status);
}

