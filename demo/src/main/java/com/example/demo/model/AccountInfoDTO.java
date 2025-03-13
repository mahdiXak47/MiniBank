package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoDTO {
    // Registration information
    private String name;
    private String nationalId;
    private LocalDate dateOfBirth;
    private String clientType;
    private String phoneNumber;
    private String address;
    private String postalCode;
    
    // Account information
    private String accountNumber;
    private LocalDateTime accountCreatedAt;
    private LocalDateTime accountExpiresAt;
    private String accountStatus;
    private LocalDateTime lastUsageDate;
    
    /**
     * Creates an AccountInfoDTO from a Client entity
     * @param client the client entity
     * @return the account information DTO
     */
    public static AccountInfoDTO fromClient(Client client) {
        AccountInfoDTO dto = new AccountInfoDTO();
        
        // Registration information
        dto.setName(client.getName());
        dto.setNationalId(client.getNationalId());
        dto.setDateOfBirth(client.getDateOfBirth());
        dto.setClientType(client.getClientType().toString());
        dto.setPhoneNumber(client.getPhoneNumber());
        dto.setAddress(client.getAddress());
        dto.setPostalCode(client.getPostalCode());
        
        // Account information
        dto.setAccountNumber(client.getAccountNumber());
        dto.setAccountCreatedAt(client.getAccountCreatedAt());
        dto.setAccountExpiresAt(client.getAccountExpiresAt());
        dto.setAccountStatus(client.getAccountStatus().toString());
        dto.setLastUsageDate(client.getLastUsageDate());
        
        return dto;
    }
} 