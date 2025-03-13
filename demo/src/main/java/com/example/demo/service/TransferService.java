package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.TransferTrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TransferService {
    private final ClientService clientService;
    private final TransferTrackingRepository transferTrackingRepository;
    
    // Fee percentage for transfers (0.1%)
    private static final BigDecimal TRANSFER_FEE_PERCENTAGE = new BigDecimal("0.001");
    
    @Autowired
    public TransferService(ClientService clientService, 
                         TransferTrackingRepository transferTrackingRepository) {
        this.clientService = clientService;
        this.transferTrackingRepository = transferTrackingRepository;
    }

    /**
     * Process a transfer request and return a tracking code
     */
    @Transactional
    public String processTransferRequest(TransferRequestDTO request) {
        // Generate tracking code
        String trackingCode = generateTrackingCode();
        
        // Create tracking record
        TransferTracking tracking = new TransferTracking();
        tracking.setTrackingCode(trackingCode);
        tracking.setType(request.getType());
        tracking.setSenderAccountNumber(request.getSenderAccountNumber());
        tracking.setReceiverAccountNumber(request.getReceiverAccountNumber());
        tracking.setAmount(request.getAmount());
        tracking.setDescription(request.getDescription());
        tracking.setRequestDate(LocalDateTime.now());
        
        try {
            // Validate request based on type
            validateRequest(request);
            
            // Calculate fee if it's a transfer
            BigDecimal fee = BigDecimal.ZERO;
            if (request.getType() == TransferType.TRANSFER) {
                fee = request.getAmount().multiply(TRANSFER_FEE_PERCENTAGE);
            }
            tracking.setFee(fee);
            
            // Process the transfer based on type
            switch (request.getType()) {
                case DEPOSIT:
                    processDeposit(request.getSenderAccountNumber(), request.getAmount());
                    break;
                case HARVEST:
                    processHarvest(request.getSenderAccountNumber(), request.getAmount());
                    break;
                case TRANSFER:
                    processTransfer(request.getSenderAccountNumber(), 
                                  request.getReceiverAccountNumber(), 
                                  request.getAmount(),
                                  fee);
                    break;
            }
            
            // Mark as completed
            tracking.setStatus(TransferTracking.TransferStatus.COMPLETED);
            tracking.setProcessDate(LocalDateTime.now());
            
        } catch (IllegalArgumentException e) {
            tracking.setStatus(TransferTracking.TransferStatus.FAILED);
            tracking.setErrorMessage(e.getMessage());
        }
        
        // Save tracking record
        transferTrackingRepository.save(tracking);
        
        return trackingCode;
    }
    
    /**
     * Get transfer status by tracking code
     */
    public TransferTracking getTransferStatus(String trackingCode) {
        return transferTrackingRepository.findByTrackingCode(trackingCode)
            .orElseThrow(() -> new IllegalArgumentException("Invalid tracking code"));
    }
    
    private void validateRequest(TransferRequestDTO request) {
        // Validate sender account exists and is active
        Client sender = clientService.getClientByAccountNumber(request.getSenderAccountNumber())
            .orElseThrow(() -> new IllegalArgumentException("Sender account not found"));
            
        if (sender.getAccountStatus() != Client.AccountStatus.ACTIVE) {
            throw new IllegalArgumentException("Sender account is " + 
                sender.getAccountStatus().toString().toLowerCase());
        }
        
        // For transfers, validate receiver account
        if (request.getType() == TransferType.TRANSFER) {
            if (request.getReceiverAccountNumber() == null) {
                throw new IllegalArgumentException("Receiver account number is required for transfers");
            }
            
            if (request.getSenderAccountNumber().equals(request.getReceiverAccountNumber())) {
                throw new IllegalArgumentException("Sender and receiver accounts cannot be the same");
            }
            
            Client receiver = clientService.getClientByAccountNumber(request.getReceiverAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Receiver account not found"));
                
            if (receiver.getAccountStatus() != Client.AccountStatus.ACTIVE) {
                throw new IllegalArgumentException("Receiver account is " + 
                    receiver.getAccountStatus().toString().toLowerCase());
            }
        }
        
        // For harvest and transfer, validate sender has sufficient funds
        if (request.getType() != TransferType.DEPOSIT) {
            BigDecimal requiredAmount = request.getAmount();
            if (request.getType() == TransferType.TRANSFER) {
                // Add fee for transfers
                requiredAmount = requiredAmount.add(
                    request.getAmount().multiply(TRANSFER_FEE_PERCENTAGE)
                );
            }
            
            if (sender.getInventory().compareTo(requiredAmount) < 0) {
                throw new IllegalArgumentException("Insufficient funds");
            }
        }
    }
    
    @Transactional
    private void processDeposit(String accountNumber, BigDecimal amount) {
        Client client = clientService.getClientByAccountNumber(accountNumber)
            .orElseThrow(() -> new IllegalArgumentException("Account not found"));
            
        client.setInventory(client.getInventory().add(amount));
        client.setLastUsageDate(LocalDateTime.now());
        clientService.saveClient(client);
    }
    
    @Transactional
    private void processHarvest(String accountNumber, BigDecimal amount) {
        Client client = clientService.getClientByAccountNumber(accountNumber)
            .orElseThrow(() -> new IllegalArgumentException("Account not found"));
            
        client.setInventory(client.getInventory().subtract(amount));
        client.setLastUsageDate(LocalDateTime.now());
        clientService.saveClient(client);
    }
    
    @Transactional
    private void processTransfer(String senderAccountNumber, String receiverAccountNumber, 
                               BigDecimal amount, BigDecimal fee) {
        // Deduct from sender (amount + fee)
        Client sender = clientService.getClientByAccountNumber(senderAccountNumber)
            .orElseThrow(() -> new IllegalArgumentException("Sender account not found"));
        sender.setInventory(sender.getInventory().subtract(amount.add(fee)));
        sender.setLastUsageDate(LocalDateTime.now());
        clientService.saveClient(sender);
        
        // Add to receiver (amount only)
        Client receiver = clientService.getClientByAccountNumber(receiverAccountNumber)
            .orElseThrow(() -> new IllegalArgumentException("Receiver account not found"));
        receiver.setInventory(receiver.getInventory().add(amount));
        receiver.setLastUsageDate(LocalDateTime.now());
        clientService.saveClient(receiver);
    }
    
    private String generateTrackingCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
} 