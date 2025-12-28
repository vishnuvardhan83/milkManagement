package com.dairyfarm.repository;

import com.dairyfarm.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByTenantId(String tenantId);
    List<ActivityLog> findByTenantIdAndModule(String tenantId, String module);
    List<ActivityLog> findByTenantIdAndCreatedAtBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate);
    List<ActivityLog> findByUserId(Long userId);
}

