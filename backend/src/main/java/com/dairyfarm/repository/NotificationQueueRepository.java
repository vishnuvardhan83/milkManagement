package com.dairyfarm.repository;

import com.dairyfarm.entity.NotificationQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationQueueRepository extends JpaRepository<NotificationQueue, Long> {
    List<NotificationQueue> findByStatus(NotificationQueue.NotificationStatus status);
    List<NotificationQueue> findByStatusOrderByCreatedAtAsc(NotificationQueue.NotificationStatus status);
    List<NotificationQueue> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
}

