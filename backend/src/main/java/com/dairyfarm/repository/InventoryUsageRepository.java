package com.dairyfarm.repository;

import com.dairyfarm.entity.InventoryItem;
import com.dairyfarm.entity.InventoryUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryUsageRepository extends JpaRepository<InventoryUsage, Long> {
    List<InventoryUsage> findByInventoryItem(InventoryItem item);
    List<InventoryUsage> findByUsageDateBetween(LocalDate startDate, LocalDate endDate);
    List<InventoryUsage> findByInventoryItemOrderByUsageDateDesc(InventoryItem item);
}

