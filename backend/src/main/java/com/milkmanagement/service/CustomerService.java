package com.milkmanagement.service;

import com.milkmanagement.dto.CustomerBalanceDTO;
import com.milkmanagement.dto.CustomerDTO;
import com.milkmanagement.entity.Customer;
import com.milkmanagement.entity.Receipt;
import com.milkmanagement.entity.User;
import com.milkmanagement.repository.CustomerRepository;
import com.milkmanagement.repository.ReceiptRepository;
import com.milkmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ReceiptRepository receiptRepository;
    
    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        return convertToDTO(customer);
    }
    
    @Transactional
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        if (customerDTO.getMobileNumber() != null && 
            customerRepository.existsByMobileNumber(customerDTO.getMobileNumber())) {
            throw new RuntimeException("Mobile number already exists");
        }
        
        if (customerDTO.getEmail() != null && !customerDTO.getEmail().isEmpty() &&
            customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        Customer customer = convertToEntity(customerDTO);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElse(null);
            customer.setCreatedBy(user);
        }
        
        Customer savedCustomer = customerRepository.save(customer);
        return convertToDTO(savedCustomer);
    }
    
    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        
        if (customerDTO.getName() != null) customer.setName(customerDTO.getName());
        if (customerDTO.getAddress() != null) customer.setAddress(customerDTO.getAddress());
        if (customerDTO.getMobileNumber() != null) customer.setMobileNumber(customerDTO.getMobileNumber());
        if (customerDTO.getEmail() != null) customer.setEmail(customerDTO.getEmail());
        if (customerDTO.getDailyMilkQuantity() != null) customer.setDailyMilkQuantity(customerDTO.getDailyMilkQuantity());
        if (customerDTO.getMilkType() != null) customer.setMilkType(customerDTO.getMilkType());
        if (customerDTO.getDeliveryStatus() != null) customer.setDeliveryStatus(customerDTO.getDeliveryStatus());
        
        Customer updatedCustomer = customerRepository.save(customer);
        return convertToDTO(updatedCustomer);
    }
    
    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public List<CustomerBalanceDTO> getCustomersWithPendingAmounts() {
        return customerRepository.findAll().stream()
                .map(customer -> {
                    BigDecimal pending = receiptRepository.getTotalPendingAmountByCustomer(customer);
                    if (pending == null) pending = BigDecimal.ZERO;
                    
                    Long pendingCount = receiptRepository.findByCustomer(customer).stream()
                            .filter(r -> r.getPaymentStatus() != Receipt.PaymentStatus.PAID)
                            .count();
                    
                    CustomerBalanceDTO dto = new CustomerBalanceDTO();
                    dto.setCustomerId(customer.getId());
                    dto.setCustomerName(customer.getName());
                    dto.setMobileNumber(customer.getMobileNumber());
                    dto.setEmail(customer.getEmail());
                    dto.setTotalPendingAmount(pending);
                    dto.setPendingReceiptsCount(pendingCount);
                    return dto;
                })
                .filter(dto -> dto.getTotalPendingAmount().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());
    }
    
    private CustomerDTO convertToDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setAddress(customer.getAddress());
        dto.setMobileNumber(customer.getMobileNumber());
        dto.setEmail(customer.getEmail());
        dto.setDailyMilkQuantity(customer.getDailyMilkQuantity());
        dto.setMilkType(customer.getMilkType());
        dto.setDeliveryStatus(customer.getDeliveryStatus());
        dto.setBalance(customer.getBalance());
        if (customer.getCreatedBy() != null) {
            dto.setCreatedById(customer.getCreatedBy().getId());
        }
        return dto;
    }
    
    private Customer convertToEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setAddress(dto.getAddress());
        customer.setMobileNumber(dto.getMobileNumber());
        customer.setEmail(dto.getEmail());
        customer.setDailyMilkQuantity(dto.getDailyMilkQuantity() != null ? dto.getDailyMilkQuantity() : java.math.BigDecimal.ZERO);
        customer.setMilkType(dto.getMilkType() != null ? dto.getMilkType() : Customer.MilkType.COW);
        customer.setDeliveryStatus(dto.getDeliveryStatus() != null ? dto.getDeliveryStatus() : Customer.DeliveryStatus.ACTIVE);
        return customer;
    }
}
