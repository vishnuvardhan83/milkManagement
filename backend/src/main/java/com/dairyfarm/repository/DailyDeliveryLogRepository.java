package com.dairyfarm.repository;

import com.dairyfarm.entity.Customer;
import com.dairyfarm.entity.DailyDeliveryLog;
import com.dairyfarm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyDeliveryLogRepository extends JpaRepository<DailyDeliveryLog, Long> {
    List<DailyDeliveryLog> findByTenantId(String tenantId);
    List<DailyDeliveryLog> findByTenantIdAndDeliveryDate(String tenantId, LocalDate date);
    List<DailyDeliveryLog> findByDeliveryBoyAndDeliveryDate(User deliveryBoy, LocalDate date);
    List<DailyDeliveryLog> findByCustomerAndDeliveryDateBetween(Customer customer, LocalDate startDate, LocalDate endDate);
    Optional<DailyDeliveryLog> findByQrCode(String qrCode);
    
    @Query("SELECT d FROM DailyDeliveryLog d WHERE d.tenantId = :tenantId AND d.deliveryDate = :date AND d.deliveryStatus = 'PENDING'")
    List<DailyDeliveryLog> findPendingDeliveriesForDate(@Param("tenantId") String tenantId, @Param("date") LocalDate date);
}

