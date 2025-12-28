package com.dairyfarm.repository;

import com.dairyfarm.entity.Animal;
import com.dairyfarm.entity.MilkEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MilkEntryRepository extends JpaRepository<MilkEntry, Long> {
    List<MilkEntry> findByEntryDate(LocalDate date);
    List<MilkEntry> findByEntryDateBetween(LocalDate startDate, LocalDate endDate);
    Optional<MilkEntry> findByAnimalAndEntryDate(Animal animal, LocalDate date);
    
    @Query("SELECT SUM(me.totalQuantity) FROM MilkEntry me WHERE me.entryDate = :date")
    java.math.BigDecimal getTotalMilkByDate(@Param("date") LocalDate date);
    
    @Query("SELECT SUM(me.totalQuantity) FROM MilkEntry me WHERE me.entryDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal getTotalMilkByDateRange(@Param("startDate") LocalDate startDate, 
                                                 @Param("endDate") LocalDate endDate);
    
    @Query("SELECT AVG(me.totalQuantity) FROM MilkEntry me WHERE me.entryDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal getAverageDailyMilk(@Param("startDate") LocalDate startDate, 
                                            @Param("endDate") LocalDate endDate);
}

