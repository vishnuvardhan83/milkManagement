package com.milkmanagement.service;

import com.milkmanagement.dto.ProductDTO;
import com.milkmanagement.dto.ProductQuantityDTO;
import com.milkmanagement.entity.Product;
import com.milkmanagement.entity.ProductPrice;
import com.milkmanagement.entity.Stock;
import com.milkmanagement.entity.User;
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
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductPriceRepository productPriceRepository;
    
    @Autowired
    private StockRepository stockRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductDTO> searchProducts(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllProducts();
        }
        
        String lowerSearchTerm = searchTerm.toLowerCase();
        return productRepository.findAll().stream()
                .filter(product -> 
                    product.getName().toLowerCase().contains(lowerSearchTerm) ||
                    (product.getDescription() != null && product.getDescription().toLowerCase().contains(lowerSearchTerm))
                )
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductDTO> filterProducts(String name, BigDecimal minPrice, BigDecimal maxPrice, String type) {
        return productRepository.findAll().stream()
                .filter(product -> {
                    boolean matches = true;
                    
                    if (name != null && !name.trim().isEmpty()) {
                        matches = matches && product.getName().toLowerCase().contains(name.toLowerCase());
                    }
                    
                    if (type != null && !type.trim().isEmpty()) {
                        // Type matching logic - you may need to adjust based on your type field
                        matches = matches && product.getName().toUpperCase().contains(type.toUpperCase());
                    }
                    
                    return matches;
                })
                .map(this::convertToDTO)
                .filter(dto -> {
                    boolean priceMatches = true;
                    if (minPrice != null) {
                        priceMatches = priceMatches && dto.getPricePerUnit().compareTo(minPrice) >= 0;
                    }
                    if (maxPrice != null) {
                        priceMatches = priceMatches && dto.getPricePerUnit().compareTo(maxPrice) <= 0;
                    }
                    return priceMatches;
                })
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return convertToDTO(product);
    }
    
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        if (productRepository.existsByName(productDTO.getName())) {
            throw new RuntimeException("Product with name '" + productDTO.getName() + "' already exists");
        }
        
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setUnit(productDTO.getUnit() != null ? productDTO.getUnit() : "LITRE");
        product.setDescription(productDTO.getDescription());
        product.setCategory(productDTO.getCategory());
        product.setMinOrderQuantity(productDTO.getMinOrderQuantity());
        product.setImageUrl(productDTO.getImageUrl());
        
        Product savedProduct = productRepository.save(product);
        
        // Create stock entry
        Stock stock = new Stock();
        stock.setProduct(savedProduct);
        stock.setQuantity(productDTO.getQuantity() != null ? productDTO.getQuantity() : BigDecimal.ZERO);
        stockRepository.save(stock);
        
        // Create product price
        ProductPrice productPrice = new ProductPrice();
        productPrice.setProduct(savedProduct);
        productPrice.setPricePerUnit(productDTO.getPricePerUnit());
        productPrice.setEffectiveFrom(LocalDate.now());
        productPrice.setIsActive(true);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            productPrice.setCreatedBy(user);
        }
        
        productPriceRepository.save(productPrice);
        
        return convertToDTO(savedProduct);
    }
    
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        // Check if name is being changed and if new name already exists
        if (!product.getName().equals(productDTO.getName()) && 
            productRepository.existsByName(productDTO.getName())) {
            throw new RuntimeException("Product with name '" + productDTO.getName() + "' already exists");
        }
        
        product.setName(productDTO.getName());
        if (productDTO.getUnit() != null) {
            product.setUnit(productDTO.getUnit());
        }
        if (productDTO.getDescription() != null) {
            product.setDescription(productDTO.getDescription());
        }
        if (productDTO.getCategory() != null) {
            product.setCategory(productDTO.getCategory());
        }
        if (productDTO.getMinOrderQuantity() != null) {
            product.setMinOrderQuantity(productDTO.getMinOrderQuantity());
        }
        if (productDTO.getImageUrl() != null) {
            product.setImageUrl(productDTO.getImageUrl());
        }
        
        Product savedProduct = productRepository.save(product);
        
        // Update stock
        Stock stock = stockRepository.findByProductId(id).orElse(new Stock());
        stock.setProduct(savedProduct);
        stock.setQuantity(productDTO.getQuantity() != null ? productDTO.getQuantity() : BigDecimal.ZERO);
        stockRepository.save(stock);
        
        // Update or create new product price
        ProductPrice existingPrice = productPriceRepository.findLatestActivePrice(savedProduct).orElse(null);
        if (existingPrice != null && existingPrice.getPricePerUnit().compareTo(productDTO.getPricePerUnit()) != 0) {
            // Deactivate old price
            existingPrice.setIsActive(false);
            existingPrice.setEffectiveTo(LocalDate.now());
            productPriceRepository.save(existingPrice);
            
            // Create new price
            ProductPrice newPrice = new ProductPrice();
            newPrice.setProduct(savedProduct);
            newPrice.setPricePerUnit(productDTO.getPricePerUnit());
            newPrice.setEffectiveFrom(LocalDate.now());
            newPrice.setIsActive(true);
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                User user = userRepository.findByUsername(username).orElse(null);
                newPrice.setCreatedBy(user);
            }
            
            productPriceRepository.save(newPrice);
        } else if (existingPrice == null) {
            // Create first price
            ProductPrice newPrice = new ProductPrice();
            newPrice.setProduct(savedProduct);
            newPrice.setPricePerUnit(productDTO.getPricePerUnit());
            newPrice.setEffectiveFrom(LocalDate.now());
            newPrice.setIsActive(true);
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                User user = userRepository.findByUsername(username).orElse(null);
                newPrice.setCreatedBy(user);
            }
            
            productPriceRepository.save(newPrice);
        }
        
        return convertToDTO(savedProduct);
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public Long getProductCount() {
        return productRepository.count();
    }
    
    @Transactional(readOnly = true)
    public ProductQuantityDTO getProductQuantities() {
        ProductQuantityDTO quantities = new ProductQuantityDTO();
        
        // Get quantities from products
        List<ProductDTO> products = getAllProducts();
        
        BigDecimal cowMilkQty = BigDecimal.ZERO;
        BigDecimal buffaloMilkQty = BigDecimal.ZERO;
        BigDecimal curdQty = BigDecimal.ZERO;
        
        for (ProductDTO product : products) {
            if ("COW_MILK".equals(product.getType())) {
                cowMilkQty = cowMilkQty.add(product.getQuantity());
            } else if ("BUFFALO_MILK".equals(product.getType())) {
                buffaloMilkQty = buffaloMilkQty.add(product.getQuantity());
            } else if ("CURD".equals(product.getType())) {
                curdQty = curdQty.add(product.getQuantity());
            }
        }
        
        // Set default values if no products found
        if (cowMilkQty.compareTo(BigDecimal.ZERO) == 0) {
            cowMilkQty = new BigDecimal("100");
        }
        if (buffaloMilkQty.compareTo(BigDecimal.ZERO) == 0) {
            buffaloMilkQty = new BigDecimal("80");
        }
        if (curdQty.compareTo(BigDecimal.ZERO) == 0) {
            curdQty = new BigDecimal("50");
        }
        
        quantities.setCowMilk(cowMilkQty);
        quantities.setBuffaloMilk(buffaloMilkQty);
        quantities.setCurd(curdQty);
        
        return quantities;
    }
    
    @Transactional
    public ProductQuantityDTO updateProductQuantity(String type, BigDecimal quantity) {
        // Update stock quantities for products of the specified type
        List<ProductDTO> products = getAllProducts();
        
        for (ProductDTO product : products) {
            if (type.equals(product.getType())) {
                // Update stock for this product
                Stock stock = stockRepository.findByProductId(product.getId()).orElse(new Stock());
                if (stock.getProduct() == null) {
                    Product prod = productRepository.findById(product.getId()).orElse(null);
                    if (prod != null) {
                        stock.setProduct(prod);
                    }
                }
                if (quantity != null) {
                    stock.setQuantity(quantity);
                    stockRepository.save(stock);
                }
            }
        }
        
        return getProductQuantities();
    }
    
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setUnit(product.getUnit());
        dto.setDescription(product.getDescription());
        dto.setCategory(product.getCategory());
        dto.setMinOrderQuantity(product.getMinOrderQuantity());
        dto.setImageUrl(product.getImageUrl());
        
        // Get stock quantity
        Stock stock = stockRepository.findByProductId(product.getId()).orElse(null);
        dto.setQuantity(stock != null ? stock.getQuantity() : BigDecimal.ZERO);
        
        // Get current price
        ProductPrice price = productPriceRepository.findLatestActivePrice(product).orElse(null);
        dto.setPricePerUnit(price != null ? price.getPricePerUnit() : BigDecimal.ZERO);
        
        // Determine type from name (you can adjust this logic)
        String nameUpper = product.getName().toUpperCase();
        if (nameUpper.contains("COW")) {
            dto.setType("COW_MILK");
        } else if (nameUpper.contains("BUFFALO")) {
            dto.setType("BUFFALO_MILK");
        } else if (nameUpper.contains("CURD")) {
            dto.setType("CURD");
        }
        
        return dto;
    }
}
