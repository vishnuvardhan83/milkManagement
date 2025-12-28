package com.milkmanagement.controller;

import com.milkmanagement.dto.DashboardStatsDTO;
import com.milkmanagement.dto.ProfitLossDTO;
import com.milkmanagement.service.DashboardService;
import com.milkmanagement.service.ProfitLossService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    
    @Autowired
    private DashboardService dashboardService;
    
    @Autowired
    private ProfitLossService profitLossService;
    
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        try {
            DashboardStatsDTO stats = dashboardService.getDashboardStats(fromDate, toDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/profit-loss")
    public ResponseEntity<ProfitLossDTO> getProfitLoss(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            ProfitLossDTO profitLoss = profitLossService.calculateProfitLoss(startDate, endDate);
            return ResponseEntity.ok(profitLoss);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
