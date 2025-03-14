package com.example.demo.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransferStatusDTO {
    private String trackingCode;
    private String status;
    private String type;
    private String senderAccountNumber;
    private String senderName;
    private String receiverAccountNumber;
    private String receiverName;
    private BigDecimal amount;
    private BigDecimal fee;
    private String description;
    private LocalDateTime requestDate;
    private LocalDateTime processDate;
    private String errorMessage;

    public static TransferStatusDTO fromTransferTracking(TransferTracking tracking, 
                                                        String senderName, 
                                                        String receiverName) {
        TransferStatusDTO dto = new TransferStatusDTO();
        dto.setTrackingCode(tracking.getTrackingCode());
        dto.setStatus(tracking.getStatus().toString());
        dto.setType(tracking.getType().toString());
        dto.setSenderAccountNumber(tracking.getSenderAccountNumber());
        dto.setSenderName(senderName);
        dto.setReceiverAccountNumber(tracking.getReceiverAccountNumber());
        dto.setReceiverName(receiverName);
        dto.setAmount(tracking.getAmount());
        dto.setFee(tracking.getFee());
        dto.setDescription(tracking.getDescription());
        dto.setRequestDate(tracking.getRequestDate());
        dto.setProcessDate(tracking.getProcessDate());
        dto.setErrorMessage(tracking.getErrorMessage());
        return dto;
    }
} 