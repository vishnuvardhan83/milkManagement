package com.milkmanagement.repository;

import com.milkmanagement.entity.Salary;
import com.milkmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    List<Salary> findByEmployee(User employee);
    Optional<Salary> findByEmployeeAndSalaryMonth(User employee, LocalDate salaryMonth);
    
    @Query("SELECT SUM(s.netSalary) FROM Salary s WHERE s.salaryMonth BETWEEN :startDate AND :endDate AND s.paymentStatus = 'PAID'")
    java.math.BigDecimal getTotalSalariesByDateRange(@Param("startDate") LocalDate startDate, 
                                                      @Param("endDate") LocalDate endDate);
}

