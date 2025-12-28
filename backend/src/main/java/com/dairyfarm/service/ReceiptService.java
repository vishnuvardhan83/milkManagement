package com.dairyfarm.service;

import com.dairyfarm.dto.ReceiptDTO;
import com.dairyfarm.entity.Customer;
import com.dairyfarm.entity.Receipt;
import com.dairyfarm.entity.User;
import com.dairyfarm.repository.CustomerRepository;
import com.dairyfarm.repository.ReceiptRepository;
import com.dairyfarm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReceiptService {
    
    @Autowired
    private ReceiptRepository receiptRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public List<ReceiptDTO> getAllReceipts() {
        return receiptRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ReceiptDTO> getReceiptsByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
        return receiptRepository.findByCustomer(customer).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ReceiptDTO getReceiptById(Long id) {
        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found with id: " + id));
        return convertToDTO(receipt);
    }
    
    @Transactional
    public ReceiptDTO createReceipt(ReceiptDTO receiptDTO) {
        Customer customer = customerRepository.findById(receiptDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + receiptDTO.getCustomerId()));
        
        Receipt receipt = new Receipt();
        receipt.setCustomer(customer);
        receipt.setReceiptDate(receiptDTO.getReceiptDate() != null ? receiptDTO.getReceiptDate() : LocalDate.now());
        receipt.setQuantityLiters(receiptDTO.getQuantityLiters());
        receipt.setMilkRate(receiptDTO.getMilkRate());
        receipt.setPaymentMethod(receiptDTO.getPaymentMethod());
        receipt.setNotes(receiptDTO.getNotes());
        
        if (receiptDTO.getPaidAmount() != null) {
            receipt.setPaidAmount(receiptDTO.getPaidAmount());
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            receipt.setCreatedBy(user);
        }
        
        Receipt saved = receiptRepository.save(receipt);
        updateCustomerBalance(customer);
        
        return convertToDTO(saved);
    }
    
    @Transactional
    public ReceiptDTO updateReceipt(Long id, ReceiptDTO receiptDTO) {
        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found with id: " + id));
        
        receipt.setQuantityLiters(receiptDTO.getQuantityLiters());
        receipt.setMilkRate(receiptDTO.getMilkRate());
        receipt.setPaymentMethod(receiptDTO.getPaymentMethod());
        receipt.setNotes(receiptDTO.getNotes());
        
        if (receiptDTO.getPaidAmount() != null) {
            receipt.setPaidAmount(receiptDTO.getPaidAmount());
        }
        if (receiptDTO.getPaymentDate() != null) {
            receipt.setPaymentDate(receiptDTO.getPaymentDate());
        }
        
        Receipt saved = receiptRepository.save(receipt);
        updateCustomerBalance(receipt.getCustomer());
        
        return convertToDTO(saved);
    }
    
    @Transactional
    public void deleteReceipt(Long id) {
        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found with id: " + id));
        Customer customer = receipt.getCustomer();
        receiptRepository.delete(receipt);
        updateCustomerBalance(customer);
    }
    
    @Transactional
    public ReceiptDTO recordPayment(Long receiptId, BigDecimal amount, String paymentMethod) {
        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new RuntimeException("Receipt not found with id: " + receiptId));
        
        BigDecimal currentPaid = receipt.getPaidAmount() != null ? receipt.getPaidAmount() : BigDecimal.ZERO;
        receipt.setPaidAmount(currentPaid.add(amount));
        receipt.setPaymentDate(LocalDate.now());
        receipt.setPaymentMethod(paymentMethod);
        
        Receipt saved = receiptRepository.save(receipt);
        updateCustomerBalance(receipt.getCustomer());
        
        return convertToDTO(saved);
    }
    
    private void updateCustomerBalance(Customer customer) {
        BigDecimal totalPending = receiptRepository.getTotalPendingAmountByCustomer(customer);
        if (totalPending == null) {
            totalPending = BigDecimal.ZERO;
        }
        customer.setBalance(totalPending);
        customerRepository.save(customer);
    }
    
    private ReceiptDTO convertToDTO(Receipt receipt) {
        ReceiptDTO dto = new ReceiptDTO();
        dto.setId(receipt.getId());
        dto.setReceiptNumber(receipt.getReceiptNumber());
        dto.setCustomerId(receipt.getCustomer().getId());
        dto.setCustomerName(receipt.getCustomer().getName());
        dto.setCustomerMobile(receipt.getCustomer().getMobileNumber());
        dto.setReceiptDate(receipt.getReceiptDate());
        dto.setQuantityLiters(receipt.getQuantityLiters());
        dto.setMilkRate(receipt.getMilkRate());
        dto.setTotalAmount(receipt.getTotalAmount());
        dto.setPaymentStatus(receipt.getPaymentStatus().name());
        dto.setPaidAmount(receipt.getPaidAmount());
        dto.setPendingAmount(receipt.getPendingAmount());
        dto.setPaymentDate(receipt.getPaymentDate());
        dto.setPaymentMethod(receipt.getPaymentMethod());
        dto.setNotes(receipt.getNotes());
        return dto;
    }
}

