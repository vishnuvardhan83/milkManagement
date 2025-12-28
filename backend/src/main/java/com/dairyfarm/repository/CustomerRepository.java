package com.dairyfarm.repository;

import com.dairyfarm.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByTenantId(String tenantId);
    List<Customer> findByDeliveryStatus(Customer.DeliveryStatus status);
    List<Customer> findByTenantIdAndDeliveryStatus(String tenantId, Customer.DeliveryStatus status);
    boolean existsByMobileNumber(String mobileNumber);
    boolean existsByEmail(String email);
    List<Customer> findByBalanceGreaterThan(java.math.BigDecimal amount);
    Optional<Customer> findByReferralCode(String referralCode);
    List<Customer> findByReferredBy(Long referredBy);
}
