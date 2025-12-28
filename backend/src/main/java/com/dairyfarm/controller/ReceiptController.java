package com.dairyfarm.controller;

import com.dairyfarm.dto.ReceiptDTO;
import com.dairyfarm.service.ReceiptService;
import com.dairyfarm.util.PDFGenerator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {
    
    @Autowired
    private ReceiptService receiptService;
    
    @Autowired
    private PDFGenerator pdfGenerator;
    
    @GetMapping
    public ResponseEntity<List<ReceiptDTO>> getAllReceipts() {
        try {
            List<ReceiptDTO> receipts = receiptService.getAllReceipts();
            return ResponseEntity.ok(receipts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ReceiptDTO>> getReceiptsByCustomer(@PathVariable Long customerId) {
        try {
            List<ReceiptDTO> receipts = receiptService.getReceiptsByCustomer(customerId);
            return ResponseEntity.ok(receipts);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ReceiptDTO> getReceiptById(@PathVariable Long id) {
        try {
            ReceiptDTO receipt = receiptService.getReceiptById(id);
            return ResponseEntity.ok(receipt);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadReceiptPDF(@PathVariable Long id) {
        try {
            ReceiptDTO receipt = receiptService.getReceiptById(id);
            byte[] pdfBytes = pdfGenerator.generateReceiptPDF(receipt);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "receipt-" + receipt.getReceiptNumber() + ".pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createReceipt(@Valid @RequestBody ReceiptDTO receiptDTO) {
        try {
            ReceiptDTO created = receiptService.createReceipt(receiptDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReceipt(@PathVariable Long id, 
                                          @Valid @RequestBody ReceiptDTO receiptDTO) {
        try {
            ReceiptDTO updated = receiptService.updateReceipt(id, receiptDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReceipt(@PathVariable Long id) {
        try {
            receiptService.deleteReceipt(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/payment")
    public ResponseEntity<?> recordPayment(@PathVariable Long id,
                                           @RequestParam BigDecimal amount,
                                           @RequestParam(required = false) String paymentMethod) {
        try {
            ReceiptDTO receipt = receiptService.recordPayment(id, amount, 
                    paymentMethod != null ? paymentMethod : "CASH");
            return ResponseEntity.ok(receipt);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}

