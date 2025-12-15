package com.milkmanagement.repository;

import com.milkmanagement.entity.Customer;
import com.milkmanagement.entity.MilkDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MilkDeliveryRepository extends JpaRepository<MilkDelivery, Long> {
    List<MilkDelivery> findByCustomer(Customer customer);
    List<MilkDelivery> findByDeliveryDate(LocalDate date);
    List<MilkDelivery> findByCustomerAndDeliveryDateBetween(Customer customer, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT md FROM MilkDelivery md WHERE md.customer = :customer " +
           "AND md.deliveryDate BETWEEN :startDate AND :endDate " +
           "ORDER BY md.deliveryDate ASC")
    List<MilkDelivery> findDeliveriesByCustomerAndDateRange(@Param("customer") Customer customer,
                                                             @Param("startDate") LocalDate startDate,
                                                             @Param("endDate") LocalDate endDate);
}
