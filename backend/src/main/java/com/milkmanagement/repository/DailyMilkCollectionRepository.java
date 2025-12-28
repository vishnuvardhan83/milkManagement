package com.milkmanagement.repository;

import com.milkmanagement.entity.Animal;
import com.milkmanagement.entity.DailyMilkCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyMilkCollectionRepository extends JpaRepository<DailyMilkCollection, Long> {
    List<DailyMilkCollection> findByCollectionDate(LocalDate date);
    List<DailyMilkCollection> findByCollectionDateBetween(LocalDate startDate, LocalDate endDate);
    Optional<DailyMilkCollection> findByAnimalAndCollectionDate(Animal animal, LocalDate date);
    
    @Query("SELECT SUM(dmc.totalQuantity) FROM DailyMilkCollection dmc WHERE dmc.collectionDate = :date")
    java.math.BigDecimal getTotalMilkByDate(@Param("date") LocalDate date);
    
    @Query("SELECT SUM(dmc.totalQuantity) FROM DailyMilkCollection dmc WHERE dmc.collectionDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal getTotalMilkByDateRange(@Param("startDate") LocalDate startDate, 
                                                 @Param("endDate") LocalDate endDate);
}

