package com.dairyfarm.util;

import com.dairyfarm.dto.ReceiptDTO;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Component
public class PDFGenerator {
    
    public byte[] generateReceiptPDF(ReceiptDTO receipt) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
            String receiptDate = receipt.getReceiptDate().format(formatter);
            
            document.add(new Paragraph("DAIRY FARM ERP")
                    .setFontSize(24)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10));
            
            document.add(new Paragraph("Milk Receipt")
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(30));
            
            document.add(new Paragraph("Customer Information")
                    .setFontSize(14)
                    .setBold()
                    .setMarginBottom(10));
            
            document.add(new Paragraph("Name: " + receipt.getCustomerName())
                    .setMarginBottom(5));
            document.add(new Paragraph("Mobile: " + receipt.getCustomerMobile())
                    .setMarginBottom(5));
            
            document.add(new Paragraph("Receipt Details")
                    .setFontSize(14)
                    .setBold()
                    .setMarginTop(20)
                    .setMarginBottom(10));
            
            document.add(new Paragraph("Receipt #: " + receipt.getReceiptNumber())
                    .setMarginBottom(5));
            document.add(new Paragraph("Date: " + receiptDate)
                    .setMarginBottom(20));
            
            Table table = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1, 1}))
                    .useAllAvailableWidth()
                    .setMarginTop(20)
                    .setMarginBottom(20);
            
            table.addHeaderCell(new Cell().add(new Paragraph("Description").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Quantity (L)").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Rate (₹)").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Amount (₹)").setBold()));
            
            table.addCell(new Cell().add(new Paragraph("Milk Purchase")));
            table.addCell(new Cell().add(new Paragraph(receipt.getQuantityLiters().toString())));
            table.addCell(new Cell().add(new Paragraph(receipt.getMilkRate().toString())));
            table.addCell(new Cell().add(new Paragraph(receipt.getTotalAmount().toString())));
            
            document.add(table);
            
            document.add(new Paragraph("Subtotal: ₹" + receipt.getTotalAmount())
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginTop(10));
            
            BigDecimal paidAmount = receipt.getPaidAmount() != null ? receipt.getPaidAmount() : BigDecimal.ZERO;
            document.add(new Paragraph("Paid Amount: ₹" + paidAmount)
                    .setTextAlignment(TextAlignment.RIGHT));
            
            BigDecimal pendingAmount = receipt.getPendingAmount() != null ? receipt.getPendingAmount() : BigDecimal.ZERO;
            document.add(new Paragraph("Pending Amount: ₹" + pendingAmount)
                    .setTextAlignment(TextAlignment.RIGHT));
            
            document.add(new Paragraph("Total Amount: ₹" + receipt.getTotalAmount())
                    .setFontSize(16)
                    .setBold()
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginTop(10));
            
            document.add(new Paragraph("Payment Status: " + receipt.getPaymentStatus())
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginTop(10));
            
            document.add(new Paragraph("Thank you for your business!")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(40)
                    .setFontSize(12));
            
            document.add(new Paragraph("This is a computer-generated receipt.")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(10)
                    .setItalic());
            
            document.close();
            
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
}
