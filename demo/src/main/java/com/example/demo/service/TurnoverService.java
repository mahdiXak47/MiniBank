package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.TransferTrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TurnoverService {
    private final TransferTrackingRepository transferTrackingRepository;
    private final ClientService clientService;

    @Autowired
    public TurnoverService(TransferTrackingRepository transferTrackingRepository,
                          ClientService clientService) {
        this.transferTrackingRepository = transferTrackingRepository;
        this.clientService = clientService;
    }

    /**
     * Get account turnover with filters and pagination
     */
    public Page<TurnoverResponseDTO> getAccountTurnover(TurnoverFilterDTO filter) {
        // Validate account exists
        if (filter.getAccountNumber() != null) {
            clientService.getClientByAccountNumber(filter.getAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        }

        // Create pageable object
        int pageSize = filter.getPageSize() != null ? filter.getPageSize() : 10;
        int pageNumber = filter.getPage() != null ? filter.getPage() : 0;
        
        // If limit is specified, adjust page size
        if (filter.getLimit() != null) {
            pageSize = Math.min(filter.getLimit(), pageSize);
        }
        
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        // Handle special case for last N transactions of specific type
        if (filter.getLimit() != null && filter.getType() != null && 
            filter.getAccountNumber() != null && 
            filter.getStartDate() == null && filter.getEndDate() == null &&
            filter.getMinAmount() == null && filter.getMaxAmount() == null) {
            
            return transferTrackingRepository
                .findLastTransactionsByAccountAndType(
                    filter.getAccountNumber(), 
                    filter.getType(), 
                    pageable)
                .map(this::convertToDTO);
        }

        // Get filtered data
        return transferTrackingRepository
            .findAllWithFilters(
                filter.getAccountNumber(),
                filter.getType(),
                filter.getOriginAccount(),
                filter.getDestinationAccount(),
                filter.getMinAmount(),
                filter.getMaxAmount(),
                filter.getStartDate(),
                filter.getEndDate(),
                pageable)
            .map(this::convertToDTO);
    }

    /**
     * Convert TransferTracking to TurnoverResponseDTO
     */
    private TurnoverResponseDTO convertToDTO(TransferTracking tracking) {
        TurnoverResponseDTO dto = new TurnoverResponseDTO();
        dto.setTrackingCode(tracking.getTrackingCode());
        dto.setType(tracking.getType());
        dto.setSenderAccountNumber(tracking.getSenderAccountNumber());
        dto.setReceiverAccountNumber(tracking.getReceiverAccountNumber());
        dto.setAmount(tracking.getAmount());
        dto.setFee(tracking.getFee());
        dto.setDescription(tracking.getDescription());
        dto.setRequestDate(tracking.getRequestDate());
        dto.setProcessDate(tracking.getProcessDate());
        dto.setStatus(tracking.getStatus().toString());

        // Get account holder names
        if (tracking.getSenderAccountNumber() != null) {
            clientService.getClientByAccountNumber(tracking.getSenderAccountNumber())
                .ifPresent(client -> dto.setSenderName(client.getName()));
        }
        if (tracking.getReceiverAccountNumber() != null) {
            clientService.getClientByAccountNumber(tracking.getReceiverAccountNumber())
                .ifPresent(client -> dto.setReceiverName(client.getName()));
        }

        return dto;
    }
} 