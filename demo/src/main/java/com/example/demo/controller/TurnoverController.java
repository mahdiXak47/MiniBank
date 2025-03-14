package com.example.demo.controller;

import com.example.demo.model.ApiResponse;
import com.example.demo.model.TurnoverFilterDTO;
import com.example.demo.model.TurnoverResponseDTO;
import com.example.demo.service.TurnoverService;
import com.example.demo.model.TransferType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/turnover")
public class TurnoverController {
    private final TurnoverService turnoverService;

    @Autowired
    public TurnoverController(TurnoverService turnoverService) {
        this.turnoverService = turnoverService;
    }

    /**
     * Get account turnover with filters
     */
    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<ApiResponse<Page<TurnoverResponseDTO>>> getAccountTurnover(
            @PathVariable String accountNumber,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String originAccount,
            @RequestParam(required = false) String destinationAccount,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Integer limit,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        try {
            TurnoverFilterDTO filter = new TurnoverFilterDTO();
            filter.setAccountNumber(accountNumber);
            filter.setType(type != null ? TransferType.valueOf(type) : null);
            filter.setOriginAccount(originAccount);
            filter.setDestinationAccount(destinationAccount);
            filter.setMinAmount(minAmount);
            filter.setMaxAmount(maxAmount);
            filter.setStartDate(startDate);
            filter.setEndDate(endDate);
            filter.setLimit(limit);
            filter.setPage(page);
            filter.setPageSize(pageSize);

            Page<TurnoverResponseDTO> result = turnoverService.getAccountTurnover(filter);
            
            String message = String.format("Found %d transactions (Page %d of %d)", 
                result.getNumberOfElements(), result.getNumber() + 1, result.getTotalPages());
            
            return ResponseEntity.ok(ApiResponse.success(message, result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get last N transactions of specific type
     */
    @GetMapping("/account/{accountNumber}/last/{limit}")
    public ResponseEntity<ApiResponse<Page<TurnoverResponseDTO>>> getLastTransactions(
            @PathVariable String accountNumber,
            @PathVariable Integer limit,
            @RequestParam(required = false) String type) {
        
        try {
            TurnoverFilterDTO filter = new TurnoverFilterDTO();
            filter.setAccountNumber(accountNumber);
            filter.setType(type != null ? TransferType.valueOf(type) : null);
            filter.setLimit(limit);
            filter.setPage(0);
            filter.setPageSize(Math.min(limit, 100)); // Cap at 100 per page

            Page<TurnoverResponseDTO> result = turnoverService.getAccountTurnover(filter);
            
            String message = String.format("Found last %d transactions", result.getNumberOfElements());
            
            return ResponseEntity.ok(ApiResponse.success(message, result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
} 