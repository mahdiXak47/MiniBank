package com.example.demo.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TurnoverResponseDTO {
    private String trackingCode;
    private TransferType type;
    private String senderAccountNumber;
    private String senderName;
    private String receiverAccountNumber;
    private String receiverName;
    private BigDecimal amount;
    private BigDecimal fee;
    private String description;
    private LocalDateTime requestDate;
    private LocalDateTime processDate;
    private String status;
} 