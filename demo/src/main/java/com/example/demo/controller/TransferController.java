package com.example.demo.controller;

import com.example.demo.model.ApiResponse;
import com.example.demo.model.TransferRequestDTO;
import com.example.demo.model.TransferStatusDTO;
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
                "Transfer request processed successfully. Use this tracking code to check the status.", 
                trackingCode));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get detailed transfer status by tracking code
     * @param trackingCode the tracking code
     * @return detailed transfer status information
     */
    @GetMapping("/status/{trackingCode}")
    public ResponseEntity<ApiResponse<TransferStatusDTO>> getTransferStatus(
            @PathVariable String trackingCode) {
        try {
            TransferStatusDTO status = transferService.getDetailedTransferStatus(trackingCode);
            String message = status.getStatus().equals("COMPLETED") ?
                "Transfer completed successfully" :
                status.getStatus().equals("FAILED") ?
                    "Transfer failed: " + status.getErrorMessage() :
                    "Transfer is pending";
                    
            return ResponseEntity.ok(ApiResponse.success(message, status));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
} 