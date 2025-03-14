package com.example.demo.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TurnoverFilterDTO {
    private String accountNumber;
    private TransferType type;
    private String originAccount;
    private String destinationAccount;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer limit;
    private Integer page;
    private Integer pageSize = 10; // Default page size
} 