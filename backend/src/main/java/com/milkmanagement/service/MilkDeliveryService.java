package com.milkmanagement.service;

import com.milkmanagement.dto.MilkDeliveryDTO;
import com.milkmanagement.entity.Customer;
import com.milkmanagement.entity.MilkDelivery;
import com.milkmanagement.entity.Product;
import com.milkmanagement.entity.ProductPrice;
import com.milkmanagement.entity.User;
import com.milkmanagement.repository.CustomerRepository;
import com.milkmanagement.repository.MilkDeliveryRepository;
import com.milkmanagement.repository.ProductPriceRepository;
import com.milkmanagement.repository.ProductRepository;
import com.milkmanagement.repository.StockRepository;
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
public class MilkDeliveryService {
    
    @Autowired
    private MilkDeliveryRepository milkDeliveryRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductPriceRepository productPriceRepository;
    
    @Autowired
    private StockRepository stockRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public List<MilkDeliveryDTO> getAllDeliveries() {
        return milkDeliveryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MilkDeliveryDTO> getDeliveriesByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
        return milkDeliveryRepository.findByCustomer(customer).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MilkDeliveryDTO> getDeliveriesByDate(LocalDate date) {
        return milkDeliveryRepository.findByDeliveryDate(date).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public MilkDeliveryDTO createDelivery(MilkDeliveryDTO deliveryDTO) {
        Customer customer = customerRepository.findById(deliveryDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + deliveryDTO.getCustomerId()));
        
        Product product = productRepository.findById(deliveryDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + deliveryDTO.getProductId()));
        
        BigDecimal pricePerUnit = deliveryDTO.getPricePerUnit();
        if (pricePerUnit == null) {
            ProductPrice productPrice = productPriceRepository.findLatestActivePrice(product)
                    .orElseThrow(() -> new RuntimeException("No active price found for product: " + product.getName()));
            pricePerUnit = productPrice.getPricePerUnit();
        }
        
        BigDecimal quantity = deliveryDTO.getQuantityDelivered();
        BigDecimal totalAmount = quantity.multiply(pricePerUnit);
        
        MilkDelivery delivery = new MilkDelivery();
        delivery.setCustomer(customer);
        delivery.setProduct(product);
        delivery.setDeliveryDate(deliveryDTO.getDeliveryDate() != null ? deliveryDTO.getDeliveryDate() : LocalDate.now());
        delivery.setQuantityDelivered(quantity);
        delivery.setPricePerUnit(pricePerUnit);
        delivery.setTotalAmount(totalAmount);
        delivery.setNotes(deliveryDTO.getNotes());
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            delivery.setDeliveredBy(user);
        }
        
        MilkDelivery savedDelivery = milkDeliveryRepository.save(delivery);
        
        return convertToDTO(savedDelivery);
    }
    
    private MilkDeliveryDTO convertToDTO(MilkDelivery delivery) {
        MilkDeliveryDTO dto = new MilkDeliveryDTO();
        dto.setId(delivery.getId());
        dto.setCustomerId(delivery.getCustomer().getId());
        dto.setProductId(delivery.getProduct().getId());
        dto.setDeliveryDate(delivery.getDeliveryDate());
        dto.setQuantityDelivered(delivery.getQuantityDelivered());
        dto.setPricePerUnit(delivery.getPricePerUnit());
        dto.setTotalAmount(delivery.getTotalAmount());
        dto.setNotes(delivery.getNotes());
        if (delivery.getDeliveredBy() != null) {
            dto.setDeliveredById(delivery.getDeliveredBy().getId());
        }
        dto.setCustomerName(delivery.getCustomer().getName());
        dto.setProductName(delivery.getProduct().getName());
        return dto;
    }
}
