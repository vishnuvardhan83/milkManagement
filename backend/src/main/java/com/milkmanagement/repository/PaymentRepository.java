package com.milkmanagement.repository;

import com.milkmanagement.entity.Customer;
import com.milkmanagement.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByCustomer(Customer customer);
    List<Payment> findByPaymentDate(LocalDate date);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.customer = :customer")
    java.math.BigDecimal getTotalPaymentsByCustomer(@Param("customer") Customer customer);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal getTotalPaymentsByDateRange(@Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);
}
