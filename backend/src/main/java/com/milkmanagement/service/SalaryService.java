package com.milkmanagement.service;

import com.milkmanagement.dto.SalaryDTO;
import com.milkmanagement.entity.Salary;
import com.milkmanagement.entity.User;
import com.milkmanagement.repository.SalaryRepository;
import com.milkmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalaryService {
    
    @Autowired
    private SalaryRepository salaryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public List<SalaryDTO> getAllSalaries() {
        return salaryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public SalaryDTO getSalaryById(Long id) {
        Salary salary = salaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salary not found with id: " + id));
        return convertToDTO(salary);
    }
    
    @Transactional
    public SalaryDTO createSalary(SalaryDTO salaryDTO) {
        User employee = userRepository.findById(salaryDTO.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + salaryDTO.getEmployeeId()));
        
        // Check if salary already exists for this month
        LocalDate salaryMonth = salaryDTO.getSalaryMonth();
        if (salaryRepository.findByEmployeeAndSalaryMonth(employee, salaryMonth).isPresent()) {
            throw new RuntimeException("Salary already exists for employee " + employee.getUsername() + " for month " + salaryMonth);
        }
        
        Salary salary = new Salary();
        salary.setEmployee(employee);
        salary.setSalaryMonth(salaryMonth);
        salary.setBaseSalary(salaryDTO.getBaseSalary());
        salary.setBonus(salaryDTO.getBonus() != null ? salaryDTO.getBonus() : java.math.BigDecimal.ZERO);
        salary.setDeductions(salaryDTO.getDeductions() != null ? salaryDTO.getDeductions() : java.math.BigDecimal.ZERO);
        if (salaryDTO.getPaymentStatus() != null) {
            salary.setPaymentStatus(Salary.PaymentStatus.valueOf(salaryDTO.getPaymentStatus()));
        }
        salary.setPaymentDate(salaryDTO.getPaymentDate());
        salary.setNotes(salaryDTO.getNotes());
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            salary.setPaidBy(user);
        }
        
        Salary saved = salaryRepository.save(salary);
        return convertToDTO(saved);
    }
    
    @Transactional
    public SalaryDTO updateSalary(Long id, SalaryDTO salaryDTO) {
        Salary salary = salaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salary not found with id: " + id));
        
        salary.setBaseSalary(salaryDTO.getBaseSalary());
        salary.setBonus(salaryDTO.getBonus() != null ? salaryDTO.getBonus() : java.math.BigDecimal.ZERO);
        salary.setDeductions(salaryDTO.getDeductions() != null ? salaryDTO.getDeductions() : java.math.BigDecimal.ZERO);
        if (salaryDTO.getPaymentStatus() != null) {
            salary.setPaymentStatus(Salary.PaymentStatus.valueOf(salaryDTO.getPaymentStatus()));
        }
        salary.setPaymentDate(salaryDTO.getPaymentDate());
        salary.setNotes(salaryDTO.getNotes());
        
        Salary saved = salaryRepository.save(salary);
        return convertToDTO(saved);
    }
    
    @Transactional
    public void deleteSalary(Long id) {
        if (!salaryRepository.existsById(id)) {
            throw new RuntimeException("Salary not found with id: " + id);
        }
        salaryRepository.deleteById(id);
    }
    
    private SalaryDTO convertToDTO(Salary salary) {
        SalaryDTO dto = new SalaryDTO();
        dto.setId(salary.getId());
        dto.setEmployeeId(salary.getEmployee().getId());
        dto.setEmployeeName(salary.getEmployee().getUsername());
        dto.setSalaryMonth(salary.getSalaryMonth());
        dto.setBaseSalary(salary.getBaseSalary());
        dto.setBonus(salary.getBonus());
        dto.setDeductions(salary.getDeductions());
        dto.setNetSalary(salary.getNetSalary());
        dto.setPaymentStatus(salary.getPaymentStatus().name());
        dto.setPaymentDate(salary.getPaymentDate());
        dto.setNotes(salary.getNotes());
        return dto;
    }
}

