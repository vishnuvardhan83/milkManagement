package com.milkmanagement.service;

import com.milkmanagement.dto.PaymentDTO;
import com.milkmanagement.entity.Customer;
import com.milkmanagement.entity.Invoice;
import com.milkmanagement.entity.Payment;
import com.milkmanagement.entity.User;
import com.milkmanagement.repository.CustomerRepository;
import com.milkmanagement.repository.InvoiceRepository;
import com.milkmanagement.repository.PaymentRepository;
import com.milkmanagement.repository.UserRepository;
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
    private UserRepository userRepository;
    
    @Autowired
    private InvoiceRepository invoiceRepository;
    
    @Transactional(readOnly = true)
    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        return convertToDTO(payment);
    }
    
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPendingPayments() {
        // Get all payments and filter those related to pending/partial invoices
        List<Payment> allPayments = paymentRepository.findAll();
        return allPayments.stream()
                .map(this::convertToDTO)
                .filter(p -> "PENDING".equals(p.getStatus()) || "PARTIAL".equals(p.getStatus()))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        Customer customer = customerRepository.findById(paymentDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + paymentDTO.getCustomerId()));
        
        Payment payment = new Payment();
        payment.setCustomer(customer);
        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentDate(paymentDTO.getPaymentDate() != null ? paymentDTO.getPaymentDate() : LocalDate.now());
        payment.setPaymentMethod(paymentDTO.getPaymentMethod() != null ? paymentDTO.getPaymentMethod() : Payment.PaymentMethod.CASH);
        payment.setReferenceNumber(paymentDTO.getReferenceNumber());
        payment.setNotes(paymentDTO.getNotes());
        
        // Set received by user if authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            payment.setReceivedBy(user);
        }
        
        Payment savedPayment = paymentRepository.save(payment);
        
        // Update invoice if invoiceId is provided
        if (paymentDTO.getInvoiceId() != null) {
            updateInvoiceWithPayment(paymentDTO.getInvoiceId(), savedPayment);
        }
        
        return convertToDTO(savedPayment);
    }
    
    @Transactional
    public PaymentDTO updatePayment(Long id, PaymentDTO paymentDTO) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        
        if (paymentDTO.getAmount() != null) {
            payment.setAmount(paymentDTO.getAmount());
        }
        if (paymentDTO.getPaymentDate() != null) {
            payment.setPaymentDate(paymentDTO.getPaymentDate());
        }
        if (paymentDTO.getPaymentMethod() != null) {
            payment.setPaymentMethod(paymentDTO.getPaymentMethod());
        }
        if (paymentDTO.getReferenceNumber() != null) {
            payment.setReferenceNumber(paymentDTO.getReferenceNumber());
        }
        if (paymentDTO.getNotes() != null) {
            payment.setNotes(paymentDTO.getNotes());
        }
        
        Payment savedPayment = paymentRepository.save(payment);
        
        // Update invoice status if status is provided
        if (paymentDTO.getStatus() != null) {
            if (paymentDTO.getInvoiceId() != null) {
                updateInvoiceStatus(paymentDTO.getInvoiceId(), paymentDTO.getStatus());
            } else {
                // Find related invoice for this payment's customer and update it
                updateRelatedInvoiceStatus(savedPayment, paymentDTO.getStatus());
            }
        }
        
        return convertToDTO(savedPayment);
    }
    
    @Transactional
    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new RuntimeException("Payment not found with id: " + id);
        }
        paymentRepository.deleteById(id);
    }
    
    private void updateInvoiceWithPayment(Long invoiceId, Payment payment) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElse(null);
        if (invoice != null) {
            BigDecimal newPaidAmount = invoice.getPaidAmount().add(payment.getAmount());
            invoice.setPaidAmount(newPaidAmount);
            invoice.setDueAmount(invoice.getTotalAmount().subtract(newPaidAmount));
            
            if (newPaidAmount.compareTo(invoice.getTotalAmount()) >= 0) {
                invoice.setStatus(Invoice.InvoiceStatus.PAID);
            } else if (newPaidAmount.compareTo(BigDecimal.ZERO) > 0) {
                invoice.setStatus(Invoice.InvoiceStatus.PARTIAL);
            } else {
                invoice.setStatus(Invoice.InvoiceStatus.PENDING);
            }
            
            invoiceRepository.save(invoice);
        }
    }
    
    private void updateInvoiceStatus(Long invoiceId, String status) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElse(null);
        if (invoice != null) {
            if ("PAID".equals(status)) {
                invoice.setStatus(Invoice.InvoiceStatus.PAID);
                invoice.setPaidAmount(invoice.getTotalAmount());
                invoice.setDueAmount(BigDecimal.ZERO);
            } else if ("PARTIAL".equals(status)) {
                invoice.setStatus(Invoice.InvoiceStatus.PARTIAL);
            } else {
                invoice.setStatus(Invoice.InvoiceStatus.PENDING);
            }
            invoiceRepository.save(invoice);
        }
    }
    
    private void updateRelatedInvoiceStatus(Payment payment, String status) {
        // Find the most recent pending/partial invoice for this customer
        List<Invoice> customerInvoices = invoiceRepository.findByCustomer(payment.getCustomer());
        Invoice relatedInvoice = customerInvoices.stream()
                .filter(inv -> inv.getStatus() != Invoice.InvoiceStatus.PAID)
                .findFirst()
                .orElse(null);
        
        if (relatedInvoice != null) {
            updateInvoiceStatus(relatedInvoice.getId(), status);
        }
    }
    
    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setCustomerId(payment.getCustomer().getId());
        dto.setCustomerName(payment.getCustomer().getName());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setReferenceNumber(payment.getReferenceNumber());
        dto.setNotes(payment.getNotes());
        dto.setReceivedById(payment.getReceivedBy() != null ? payment.getReceivedBy().getId() : null);
        
        // Find related invoice for this customer and set status
        List<Invoice> customerInvoices = invoiceRepository.findByCustomer(payment.getCustomer());
        if (!customerInvoices.isEmpty()) {
            // Find the most recent pending/partial invoice
            Invoice relatedInvoice = customerInvoices.stream()
                    .filter(inv -> inv.getStatus() != Invoice.InvoiceStatus.PAID)
                    .findFirst()
                    .orElse(null);
            
            if (relatedInvoice != null) {
                dto.setInvoiceId(relatedInvoice.getId());
                dto.setStatus(relatedInvoice.getStatus().name());
            } else {
                dto.setStatus("PAID");
            }
        } else {
            dto.setStatus("PENDING");
        }
        
        return dto;
    }
}
