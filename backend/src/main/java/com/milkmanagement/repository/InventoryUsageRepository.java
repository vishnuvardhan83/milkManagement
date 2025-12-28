package com.milkmanagement.repository;

import com.milkmanagement.entity.InventoryItem;
import com.milkmanagement.entity.InventoryUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryUsageRepository extends JpaRepository<InventoryUsage, Long> {
    List<InventoryUsage> findByInventoryItem(InventoryItem item);
    List<InventoryUsage> findByUsageDateBetween(LocalDate startDate, LocalDate endDate);
}

