package com.milkmanagement.repository;

import com.milkmanagement.entity.Product;
import com.milkmanagement.entity.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {
    @Query("SELECT pp FROM ProductPrice pp WHERE pp.product = :product " +
           "AND pp.isActive = true " +
           "AND (pp.effectiveTo IS NULL OR pp.effectiveTo >= :date) " +
           "AND pp.effectiveFrom <= :date " +
           "ORDER BY pp.effectiveFrom DESC")
    Optional<ProductPrice> findActivePriceByProductAndDate(@Param("product") Product product, 
                                                             @Param("date") LocalDate date);
    
    @Query("SELECT pp FROM ProductPrice pp WHERE pp.product = :product " +
           "AND pp.isActive = true " +
           "ORDER BY pp.effectiveFrom DESC")
    Optional<ProductPrice> findLatestActivePrice(@Param("product") Product product);
}
