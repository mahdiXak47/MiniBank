package com.example.demo.controller;

import com.example.demo.model.ApiResponse;
import com.example.demo.model.TransferRequestDTO;
import com.example.demo.model.TransferTracking;
import com.example.demo.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {
    private final TransferService transferService;

    @Autowired
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    /**
     * Process a transfer request
     * @param request the transfer request
     * @return tracking code for the transfer
     */
    @PostMapping("/request")
    public ResponseEntity<ApiResponse<String>> requestTransfer(
            @Valid @RequestBody TransferRequestDTO request) {
        try {
            String trackingCode = transferService.processTransferRequest(request);
            return ResponseEntity.ok(ApiResponse.success(
                "Transfer request processed successfully", trackingCode));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get transfer status by tracking code
     * @param trackingCode the tracking code
     * @return transfer status information
     */
    @GetMapping("/status/{trackingCode}")
    public ResponseEntity<ApiResponse<TransferTracking>> getTransferStatus(
            @PathVariable String trackingCode) {
        try {
            TransferTracking status = transferService.getTransferStatus(trackingCode);
            return ResponseEntity.ok(ApiResponse.success(status));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
} 