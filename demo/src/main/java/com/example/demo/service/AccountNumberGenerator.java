package com.example.demo.service;

import com.example.demo.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class AccountNumberGenerator {

    private static final int ACCOUNT_NUMBER_LENGTH = 14;
    private final SecureRandom random = new SecureRandom();
    private final ClientRepository clientRepository;

    public AccountNumberGenerator(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = generateRandomAccountNumber();
        } while (clientRepository.existsByAccountNumber(accountNumber));
        
        return accountNumber;
    }

    private String generateRandomAccountNumber() {
        StringBuilder sb = new StringBuilder(ACCOUNT_NUMBER_LENGTH);
        // First digit should not be 0
        sb.append(1 + random.nextInt(9));
        
        // Generate the remaining 13 digits
        for (int i = 1; i < ACCOUNT_NUMBER_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        
        return sb.toString();
    }
} 