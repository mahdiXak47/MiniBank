package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "clients", 
       indexes = {
           @Index(name = "idx_national_id", columnList = "national_id"),
           @Index(name = "idx_account_number", columnList = "account_number")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "national_id", nullable = false, unique = true, length = 255)
    private String nationalId;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "client_type", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private ClientType clientType;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 10)
    private String postalCode;

    @Column(name = "account_number", unique = true, length = 14)
    private String accountNumber;

    @Column(name = "account_created_at")
    private LocalDateTime accountCreatedAt;

    @Column(name = "account_expires_at")
    private LocalDateTime accountExpiresAt;
    
    @Column(name = "last_usage_date")
    private LocalDateTime lastUsageDate;
    
    @Column(name = "account_status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    @Column(name = "inventory", nullable = false, precision = 19, scale = 4)
    private BigDecimal inventory = BigDecimal.ZERO;

    public enum ClientType {
        REAL, LEGAL
    }
    
    public enum AccountStatus {
        ACTIVE, INACTIVE, BANNED
    }
} 