package com.dairyfarm.repository;

import com.dairyfarm.entity.Customer;
import com.dairyfarm.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByTenantId(String tenantId);
    List<Subscription> findByCustomer(Customer customer);
    List<Subscription> findByTenantIdAndStatus(String tenantId, Subscription.SubscriptionStatus status);
    Optional<Subscription> findBySubscriptionNumber(String subscriptionNumber);
    
    @Query("SELECT s FROM Subscription s WHERE s.tenantId = :tenantId AND s.nextBillingDate <= :date AND s.autoRenew = true AND s.status = 'ACTIVE'")
    List<Subscription> findSubscriptionsDueForRenewal(@Param("tenantId") String tenantId, @Param("date") LocalDate date);
    
    @Query("SELECT s FROM Subscription s WHERE s.tenantId = :tenantId AND s.status = 'ACTIVE' AND s.paused = false")
    List<Subscription> findActiveSubscriptions(@Param("tenantId") String tenantId);
}

