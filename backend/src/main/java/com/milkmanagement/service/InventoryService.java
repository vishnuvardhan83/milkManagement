package com.milkmanagement.service;

import com.milkmanagement.dto.InventoryEntryDTO;
import com.milkmanagement.dto.InventoryStatusDTO;
import com.milkmanagement.dto.InventoryUpdateRequest;
import com.milkmanagement.entity.InventoryEntry;
import com.milkmanagement.entity.Product;
import com.milkmanagement.entity.ProductPrice;
import com.milkmanagement.entity.Stock;
import com.milkmanagement.entity.User;
import com.milkmanagement.repository.InventoryEntryRepository;
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
public class InventoryService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ProductPriceRepository productPriceRepository;

    @Autowired
    private InventoryEntryRepository inventoryEntryRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public InventoryStatusDTO getInventoryStatus(Long productId) {
        Product product = resolveProduct(productId);
        if (product == null) {
            return null;
        }

        Stock stock = stockRepository.findByProductId(product.getId()).orElse(null);
        BigDecimal available = stock != null ? stock.getQuantity() : BigDecimal.ZERO;

        ProductPrice latestPrice = productPriceRepository.findLatestActivePrice(product).orElse(null);
        BigDecimal pricePerLitre = latestPrice != null ? latestPrice.getPricePerUnit() : BigDecimal.ZERO;

        return new InventoryStatusDTO(
                product.getId(),
                product.getName(),
                LocalDate.now(),
                available,
                available,
                pricePerLitre
        );
    }

    @Transactional
    public InventoryStatusDTO updateInventory(InventoryUpdateRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));

        // Create inventory entry
        InventoryEntry entry = new InventoryEntry();
        entry.setProduct(product);
        entry.setEntryDate(request.getDate() != null ? request.getDate() : LocalDate.now());
        entry.setTotalLitersReceived(request.getTotalLitersReceived());
        entry.setPricePerLitre(request.getPricePerLitre());
        
        // Set created by user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            entry.setCreatedBy(user);
        }
        
        inventoryEntryRepository.save(entry);

        // Update stock: add received liters
        Stock stock = stockRepository.findByProductId(product.getId()).orElse(new Stock());
        if (stock.getProduct() == null) {
            stock.setProduct(product);
        }
        BigDecimal currentQty = stock.getQuantity() != null ? stock.getQuantity() : BigDecimal.ZERO;
        stock.setQuantity(currentQty.add(request.getTotalLitersReceived()));
        stockRepository.save(stock);

        // Update price if changed
        ProductPrice existingPrice = productPriceRepository.findLatestActivePrice(product).orElse(null);
        if (existingPrice == null || existingPrice.getPricePerUnit().compareTo(request.getPricePerLitre()) != 0) {
            if (existingPrice != null) {
                existingPrice.setIsActive(false);
                existingPrice.setEffectiveTo(LocalDate.now());
                productPriceRepository.save(existingPrice);
            }

            ProductPrice newPrice = new ProductPrice();
            newPrice.setProduct(product);
            newPrice.setPricePerUnit(request.getPricePerLitre());
            newPrice.setEffectiveFrom(request.getDate() != null ? request.getDate() : LocalDate.now());
            newPrice.setIsActive(true);
            productPriceRepository.save(newPrice);
        }

        return getInventoryStatus(product.getId());
    }

    @Transactional(readOnly = true)
    public List<InventoryEntryDTO> getAllInventoryEntries(Long productId) {
        List<InventoryEntry> entries;
        if (productId != null) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
            entries = inventoryEntryRepository.findByProductOrderByDateDesc(product);
        } else {
            entries = inventoryEntryRepository.findAllOrderByDateDesc();
        }
        return entries.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InventoryEntryDTO getInventoryEntryById(Long id) {
        InventoryEntry entry = inventoryEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory entry not found with id: " + id));
        return convertToDTO(entry);
    }

    @Transactional
    public InventoryEntryDTO updateInventoryEntry(Long id, InventoryUpdateRequest request) {
        InventoryEntry entry = inventoryEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory entry not found with id: " + id));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));

        // Calculate stock difference
        BigDecimal oldQuantity = entry.getTotalLitersReceived();
        BigDecimal newQuantity = request.getTotalLitersReceived();
        BigDecimal difference = newQuantity.subtract(oldQuantity);

        // Update entry
        entry.setProduct(product);
        entry.setEntryDate(request.getDate() != null ? request.getDate() : LocalDate.now());
        entry.setTotalLitersReceived(request.getTotalLitersReceived());
        entry.setPricePerLitre(request.getPricePerLitre());
        inventoryEntryRepository.save(entry);

        // Update stock with difference
        Stock stock = stockRepository.findByProductId(product.getId()).orElse(new Stock());
        if (stock.getProduct() == null) {
            stock.setProduct(product);
        }
        BigDecimal currentQty = stock.getQuantity() != null ? stock.getQuantity() : BigDecimal.ZERO;
        stock.setQuantity(currentQty.add(difference));
        stockRepository.save(stock);

        // Update price if changed
        ProductPrice existingPrice = productPriceRepository.findLatestActivePrice(product).orElse(null);
        if (existingPrice == null || existingPrice.getPricePerUnit().compareTo(request.getPricePerLitre()) != 0) {
            if (existingPrice != null) {
                existingPrice.setIsActive(false);
                existingPrice.setEffectiveTo(LocalDate.now());
                productPriceRepository.save(existingPrice);
            }

            ProductPrice newPrice = new ProductPrice();
            newPrice.setProduct(product);
            newPrice.setPricePerUnit(request.getPricePerLitre());
            newPrice.setEffectiveFrom(request.getDate() != null ? request.getDate() : LocalDate.now());
            newPrice.setIsActive(true);
            productPriceRepository.save(newPrice);
        }

        return convertToDTO(entry);
    }

    @Transactional
    public void deleteInventoryEntry(Long id) {
        InventoryEntry entry = inventoryEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory entry not found with id: " + id));

        // Subtract from stock
        Product product = entry.getProduct();
        Stock stock = stockRepository.findByProductId(product.getId()).orElse(null);
        if (stock != null && stock.getQuantity() != null) {
            BigDecimal currentQty = stock.getQuantity();
            BigDecimal newQty = currentQty.subtract(entry.getTotalLitersReceived());
            if (newQty.compareTo(BigDecimal.ZERO) < 0) {
                newQty = BigDecimal.ZERO;
            }
            stock.setQuantity(newQty);
            stockRepository.save(stock);
        }

        inventoryEntryRepository.delete(entry);
    }

    private InventoryEntryDTO convertToDTO(InventoryEntry entry) {
        return new InventoryEntryDTO(
                entry.getId(),
                entry.getProduct().getId(),
                entry.getProduct().getName(),
                entry.getEntryDate(),
                entry.getTotalLitersReceived(),
                entry.getPricePerLitre(),
                entry.getCreatedAt(),
                entry.getUpdatedAt()
        );
    }

    private Product resolveProduct(Long productId) {
        if (productId != null) {
            return productRepository.findById(productId).orElse(null);
        }
        // Fallback: pick first product if available
        return productRepository.findAll().stream().findFirst().orElse(null);
    }
}
