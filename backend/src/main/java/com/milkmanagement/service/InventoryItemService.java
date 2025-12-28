package com.milkmanagement.service;

import com.milkmanagement.dto.InventoryItemDTO;
import com.milkmanagement.dto.InventoryUsageDTO;
import com.milkmanagement.entity.InventoryItem;
import com.milkmanagement.entity.InventoryUsage;
import com.milkmanagement.entity.User;
import com.milkmanagement.repository.InventoryItemRepository;
import com.milkmanagement.repository.InventoryUsageRepository;
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
public class InventoryItemService {
    
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    
    @Autowired
    private InventoryUsageRepository usageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public List<InventoryItemDTO> getAllInventoryItems() {
        return inventoryItemRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public InventoryItemDTO getInventoryItemById(Long id) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory item not found with id: " + id));
        return convertToDTO(item);
    }
    
    @Transactional
    public InventoryItemDTO createInventoryItem(InventoryItemDTO itemDTO) {
        InventoryItem item = new InventoryItem();
        item.setName(itemDTO.getName());
        item.setCategory(InventoryItem.InventoryCategory.valueOf(itemDTO.getCategory()));
        item.setQuantity(itemDTO.getQuantity());
        item.setUnit(itemDTO.getUnit());
        item.setCostPerUnit(itemDTO.getCostPerUnit());
        item.setSupplierName(itemDTO.getSupplierName());
        item.setPurchaseDate(itemDTO.getPurchaseDate());
        item.setExpiryDate(itemDTO.getExpiryDate());
        item.setNotes(itemDTO.getNotes());
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            item.setCreatedBy(user);
        }
        
        InventoryItem saved = inventoryItemRepository.save(item);
        return convertToDTO(saved);
    }
    
    @Transactional
    public InventoryItemDTO updateInventoryItem(Long id, InventoryItemDTO itemDTO) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory item not found with id: " + id));
        
        item.setName(itemDTO.getName());
        item.setCategory(InventoryItem.InventoryCategory.valueOf(itemDTO.getCategory()));
        item.setQuantity(itemDTO.getQuantity());
        item.setUnit(itemDTO.getUnit());
        item.setCostPerUnit(itemDTO.getCostPerUnit());
        item.setSupplierName(itemDTO.getSupplierName());
        item.setPurchaseDate(itemDTO.getPurchaseDate());
        item.setExpiryDate(itemDTO.getExpiryDate());
        item.setNotes(itemDTO.getNotes());
        
        InventoryItem saved = inventoryItemRepository.save(item);
        return convertToDTO(saved);
    }
    
    @Transactional
    public void deleteInventoryItem(Long id) {
        if (!inventoryItemRepository.existsById(id)) {
            throw new RuntimeException("Inventory item not found with id: " + id);
        }
        inventoryItemRepository.deleteById(id);
    }
    
    @Transactional
    public InventoryUsageDTO useInventoryItem(Long itemId, InventoryUsageDTO usageDTO) {
        InventoryItem item = inventoryItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Inventory item not found with id: " + itemId));
        
        if (item.getQuantity().compareTo(usageDTO.getQuantityUsed()) < 0) {
            throw new RuntimeException("Insufficient quantity. Available: " + item.getQuantity());
        }
        
        InventoryUsage usage = new InventoryUsage();
        usage.setInventoryItem(item);
        usage.setUsageDate(usageDTO.getUsageDate() != null ? usageDTO.getUsageDate() : java.time.LocalDate.now());
        usage.setQuantityUsed(usageDTO.getQuantityUsed());
        usage.setPurpose(usageDTO.getPurpose());
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            usage.setUsedBy(user);
        }
        
        usageRepository.save(usage);
        
        item.setQuantity(item.getQuantity().subtract(usageDTO.getQuantityUsed()));
        inventoryItemRepository.save(item);
        
        InventoryUsageDTO result = new InventoryUsageDTO();
        result.setId(usage.getId());
        result.setInventoryItemId(item.getId());
        result.setInventoryItemName(item.getName());
        result.setUsageDate(usage.getUsageDate());
        result.setQuantityUsed(usage.getQuantityUsed());
        result.setPurpose(usage.getPurpose());
        
        return result;
    }
    
    @Transactional(readOnly = true)
    public List<InventoryUsageDTO> getUsageHistory(Long itemId) {
        InventoryItem item = inventoryItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Inventory item not found with id: " + itemId));
        
        return usageRepository.findByInventoryItem(item).stream()
                .map(usage -> {
                    InventoryUsageDTO dto = new InventoryUsageDTO();
                    dto.setId(usage.getId());
                    dto.setInventoryItemId(item.getId());
                    dto.setInventoryItemName(item.getName());
                    dto.setUsageDate(usage.getUsageDate());
                    dto.setQuantityUsed(usage.getQuantityUsed());
                    dto.setPurpose(usage.getPurpose());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    private InventoryItemDTO convertToDTO(InventoryItem item) {
        InventoryItemDTO dto = new InventoryItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setCategory(item.getCategory().name());
        dto.setQuantity(item.getQuantity());
        dto.setUnit(item.getUnit());
        dto.setCostPerUnit(item.getCostPerUnit());
        dto.setTotalCost(item.getTotalCost());
        dto.setSupplierName(item.getSupplierName());
        dto.setPurchaseDate(item.getPurchaseDate());
        dto.setExpiryDate(item.getExpiryDate());
        dto.setNotes(item.getNotes());
        return dto;
    }
}

