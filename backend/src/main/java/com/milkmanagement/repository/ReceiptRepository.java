package com.milkmanagement.repository;

import com.milkmanagement.entity.Customer;
import com.milkmanagement.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    List<Receipt> findByCustomer(Customer customer);
    List<Receipt> findByReceiptDateBetween(LocalDate startDate, LocalDate endDate);
    Optional<Receipt> findByReceiptNumber(String receiptNumber);
    
    @Query("SELECT SUM(r.pendingAmount) FROM Receipt r WHERE r.customer = :customer AND r.paymentStatus != 'PAID'")
    java.math.BigDecimal getTotalPendingAmountByCustomer(@Param("customer") Customer customer);
    
    @Query("SELECT SUM(r.totalAmount) FROM Receipt r WHERE r.receiptDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal getTotalSalesByDateRange(@Param("startDate") LocalDate startDate, 
                                                   @Param("endDate") LocalDate endDate);
}

