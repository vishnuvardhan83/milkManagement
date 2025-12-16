package com.milkmanagement.repository;

import com.milkmanagement.entity.InventoryEntry;
import com.milkmanagement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryEntryRepository extends JpaRepository<InventoryEntry, Long> {
    List<InventoryEntry> findByProduct(Product product);
    List<InventoryEntry> findByEntryDate(LocalDate date);
    List<InventoryEntry> findByProductAndEntryDate(Product product, LocalDate date);
    
    @Query("SELECT ie FROM InventoryEntry ie ORDER BY ie.entryDate DESC, ie.createdAt DESC")
    List<InventoryEntry> findAllOrderByDateDesc();
    
    @Query("SELECT ie FROM InventoryEntry ie WHERE ie.product = :product ORDER BY ie.entryDate DESC, ie.createdAt DESC")
    List<InventoryEntry> findByProductOrderByDateDesc(@Param("product") Product product);
}

