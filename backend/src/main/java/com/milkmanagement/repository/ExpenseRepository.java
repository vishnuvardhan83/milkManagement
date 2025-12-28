package com.milkmanagement.repository;

import com.milkmanagement.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByExpenseDateBetween(LocalDate startDate, LocalDate endDate);
    List<Expense> findByCategory(Expense.ExpenseCategory category);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.expenseDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal getTotalExpensesByDateRange(@Param("startDate") LocalDate startDate, 
                                                      @Param("endDate") LocalDate endDate);
}

