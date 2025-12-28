package com.dairyfarm.repository;

import com.dairyfarm.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByModule(String module);
    List<AuditLog> findByUserId(Long userId);
    List<AuditLog> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}

