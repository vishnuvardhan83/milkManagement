package com.dairyfarm.service;

import com.dairyfarm.dto.ExpenseDTO;
import com.dairyfarm.entity.Expense;
import com.dairyfarm.entity.User;
import com.dairyfarm.repository.ExpenseRepository;
import com.dairyfarm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public List<ExpenseDTO> getAllExpenses() {
        return expenseRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ExpenseDTO> getExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByExpenseDateBetween(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ExpenseDTO getExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
        return convertToDTO(expense);
    }
    
    @Transactional
    public ExpenseDTO createExpense(ExpenseDTO expenseDTO) {
        Expense expense = new Expense();
        expense.setExpenseDate(expenseDTO.getExpenseDate() != null ? expenseDTO.getExpenseDate() : LocalDate.now());
        expense.setCategory(Expense.ExpenseCategory.valueOf(expenseDTO.getCategory()));
        expense.setDescription(expenseDTO.getDescription());
        expense.setAmount(expenseDTO.getAmount());
        expense.setPaymentMethod(expenseDTO.getPaymentMethod());
        expense.setReceiptNumber(expenseDTO.getReceiptNumber());
        expense.setNotes(expenseDTO.getNotes());
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            expense.setCreatedBy(user);
        }
        
        Expense saved = expenseRepository.save(expense);
        return convertToDTO(saved);
    }
    
    @Transactional
    public ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
        
        expense.setExpenseDate(expenseDTO.getExpenseDate());
        expense.setCategory(Expense.ExpenseCategory.valueOf(expenseDTO.getCategory()));
        expense.setDescription(expenseDTO.getDescription());
        expense.setAmount(expenseDTO.getAmount());
        expense.setPaymentMethod(expenseDTO.getPaymentMethod());
        expense.setReceiptNumber(expenseDTO.getReceiptNumber());
        expense.setNotes(expenseDTO.getNotes());
        
        Expense saved = expenseRepository.save(expense);
        return convertToDTO(saved);
    }
    
    @Transactional
    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new RuntimeException("Expense not found with id: " + id);
        }
        expenseRepository.deleteById(id);
    }
    
    private ExpenseDTO convertToDTO(Expense expense) {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setId(expense.getId());
        dto.setExpenseDate(expense.getExpenseDate());
        dto.setCategory(expense.getCategory().name());
        dto.setDescription(expense.getDescription());
        dto.setAmount(expense.getAmount());
        dto.setPaymentMethod(expense.getPaymentMethod());
        dto.setReceiptNumber(expense.getReceiptNumber());
        dto.setNotes(expense.getNotes());
        return dto;
    }
}

