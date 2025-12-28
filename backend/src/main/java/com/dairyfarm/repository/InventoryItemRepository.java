package com.dairyfarm.repository;

import com.dairyfarm.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByCategory(InventoryItem.InventoryCategory category);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.name LIKE %:search% OR i.category = :category")
    List<InventoryItem> searchByNameOrCategory(@Param("search") String search, 
                                               @Param("category") InventoryItem.InventoryCategory category);
    
    @Query("SELECT SUM(i.totalCost) FROM InventoryItem i")
    java.math.BigDecimal getTotalInventoryValue();
    
    @Query("SELECT i FROM InventoryItem i WHERE i.quantity <= i.lowStockThreshold")
    List<InventoryItem> findLowStockItems();
}

