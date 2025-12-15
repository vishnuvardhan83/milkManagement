package com.milkmanagement.repository;

import com.milkmanagement.entity.Customer;
import com.milkmanagement.entity.Customer.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByDeliveryStatus(DeliveryStatus status);
    boolean existsByMobileNumber(String mobileNumber);
    boolean existsByEmail(String email);
}
