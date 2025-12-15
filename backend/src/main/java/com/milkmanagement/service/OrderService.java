package com.milkmanagement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.milkmanagement.dto.OrderDTO;
import com.milkmanagement.dto.OrderItemDTO;
import com.milkmanagement.entity.*;
import com.milkmanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StockRepository stockRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        
        // Set customer if provided
        if (orderDTO.getCustomerId() != null) {
            Customer customer = customerRepository.findById(orderDTO.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            order.setCustomer(customer);
        }
        
        // Set user from authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            order.setUser(user);
        }
        
        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        
        // Store payment data as JSON string
        if (orderDTO.getPaymentData() != null) {
            try {
                order.setPaymentData(objectMapper.writeValueAsString(orderDTO.getPaymentData()));
            } catch (Exception e) {
                throw new RuntimeException("Error serializing payment data", e);
            }
        }
        
        Order savedOrder = orderRepository.save(order);
        
        // Create order items
        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemDTO.getProductId()));
            item.setProduct(product);
            
            item.setQuantity(itemDTO.getQuantity());
            item.setPrice(itemDTO.getPrice());
            item.setSubtotal(itemDTO.getPrice().multiply(itemDTO.getQuantity()));
            
            orderItemRepository.save(item);
            
            // Update stock
            Stock stock = stockRepository.findByProductId(product.getId()).orElse(new Stock());
            if (stock.getProduct() == null) {
                stock.setProduct(product);
            }
            BigDecimal newQuantity = stock.getQuantity().subtract(itemDTO.getQuantity());
            if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
                newQuantity = BigDecimal.ZERO;
            }
            stock.setQuantity(newQuantity);
            stockRepository.save(stock);
        }
        
        return convertToDTO(savedOrder);
    }
    
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToDTO(order);
    }
    
    @Transactional(readOnly = true)
    public List<OrderDTO> getCustomerOrders(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<OrderDTO> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setCustomerId(order.getCustomer() != null ? order.getCustomer().getId() : null);
        dto.setUserId(order.getUser() != null ? order.getUser().getId() : null);
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus() != null ? order.getStatus().name() : "PENDING");
        dto.setOrderDate(order.getOrderDate());
        
        // Convert order items
        List<OrderItemDTO> items = order.getItems().stream()
                .map(item -> {
                    OrderItemDTO itemDTO = new OrderItemDTO();
                    itemDTO.setProductId(item.getProduct().getId());
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setPrice(item.getPrice());
                    return itemDTO;
                })
                .collect(Collectors.toList());
        dto.setItems(items);
        
        // Parse payment data from JSON
        if (order.getPaymentData() != null) {
            try {
                dto.setPaymentData(objectMapper.readValue(order.getPaymentData(), Object.class));
            } catch (Exception e) {
                // If parsing fails, set as null
                dto.setPaymentData(null);
            }
        }
        
        return dto;
    }
}
