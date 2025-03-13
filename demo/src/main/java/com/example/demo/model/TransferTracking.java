package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfer_tracking")
@Data
@NoArgsConstructor
public class TransferTracking {
    @Id
    @Column(length = 20)
    private String trackingCode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransferType type;

    @Column(name = "sender_account", length = 14)
    private String senderAccountNumber;

    @Column(name = "receiver_account", length = 14)
    private String receiverAccountNumber;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal fee;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private LocalDateTime requestDate;

    @Column
    private LocalDateTime processDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransferStatus status = TransferStatus.PENDING;

    @Column(length = 500)
    private String errorMessage;

    public enum TransferStatus {
        PENDING,
        COMPLETED,
        FAILED
    }
} 