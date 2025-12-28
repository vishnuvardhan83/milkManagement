package com.dairyfarm.repository;

import com.dairyfarm.entity.Customer;
import com.dairyfarm.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByCustomer(Customer customer);
    List<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
    List<Payment> findByStatus(Payment.PaymentStatus status);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate AND p.status = 'COMPLETED'")
    java.math.BigDecimal getTotalPaymentsByDateRange(@Param("startDate") LocalDate startDate, 
                                                     @Param("endDate") LocalDate endDate);
}

