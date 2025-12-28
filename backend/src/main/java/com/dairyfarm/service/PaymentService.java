package com.dairyfarm.service;

import com.dairyfarm.dto.PaymentDTO;
import com.dairyfarm.entity.Customer;
import com.dairyfarm.entity.Payment;
import com.dairyfarm.entity.Receipt;
import com.dairyfarm.entity.User;
import com.dairyfarm.repository.CustomerRepository;
import com.dairyfarm.repository.PaymentRepository;
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
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private ReceiptRepository receiptRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return paymentRepository.findByCustomer(customer).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return convertToDTO(payment);
    }
    
    @Transactional
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        Customer customer = customerRepository.findById(paymentDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Receipt receipt = null;
        if (paymentDTO.getReceiptId() != null) {
            receipt = receiptRepository.findById(paymentDTO.getReceiptId())
                    .orElseThrow(() -> new RuntimeException("Receipt not found"));
        }
        
        Payment payment = new Payment();
        payment.setCustomer(customer);
        payment.setReceipt(receipt);
        payment.setPaymentDate(paymentDTO.getPaymentDate() != null ? paymentDTO.getPaymentDate() : LocalDate.now());
        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentMethod(Payment.PaymentMethod.valueOf(paymentDTO.getPaymentMethod()));
        payment.setReferenceNumber(paymentDTO.getReferenceNumber());
        payment.setNotes(paymentDTO.getNotes());
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            payment.setReceivedBy(user);
        }
        
        Payment saved = paymentRepository.save(payment);
        
        if (receipt != null) {
            BigDecimal currentPaid = receipt.getPaidAmount() != null ? receipt.getPaidAmount() : BigDecimal.ZERO;
            receipt.setPaidAmount(currentPaid.add(paymentDTO.getAmount()));
            receipt.setPaymentDate(LocalDate.now());
            receiptRepository.save(receipt);
        }
        
        return convertToDTO(saved);
    }
    
    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setCustomerId(payment.getCustomer().getId());
        dto.setCustomerName(payment.getCustomer().getName());
        if (payment.getReceipt() != null) {
            dto.setReceiptId(payment.getReceipt().getId());
            dto.setReceiptNumber(payment.getReceipt().getReceiptNumber());
        }
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod().name());
        dto.setReferenceNumber(payment.getReferenceNumber());
        dto.setStatus(payment.getStatus().name());
        dto.setNotes(payment.getNotes());
        return dto;
    }
}

