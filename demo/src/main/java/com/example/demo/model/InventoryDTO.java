package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDTO {
    private String accountNumber;
    private String accountHolder;
    private BigDecimal balance;
    private String accountStatus;
    private LocalDateTime lastUsageDate;
    
    /**
     * Creates an InventoryDTO from a Client entity
     * @param client the client entity
     * @return the inventory information DTO
     */
    public static InventoryDTO fromClient(Client client) {
        InventoryDTO dto = new InventoryDTO();
        dto.setAccountNumber(client.getAccountNumber());
        dto.setAccountHolder(client.getName());
        dto.setBalance(client.getInventory());
        dto.setAccountStatus(client.getAccountStatus().toString());
        dto.setLastUsageDate(client.getLastUsageDate());
        return dto;
    }
} 