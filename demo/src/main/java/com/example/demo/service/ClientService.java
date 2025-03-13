package com.example.demo.service;

import com.example.demo.model.Client;
import com.example.demo.model.ClientChangeLog;
import com.example.demo.model.ClientDTO;
import com.example.demo.model.ClientUpdateDTO;
import com.example.demo.model.AccountInfoDTO;
import com.example.demo.repository.ClientChangeLogRepository;
import com.example.demo.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientChangeLogRepository changeLogRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    @Autowired
    public ClientService(ClientRepository clientRepository, 
                         ClientChangeLogRepository changeLogRepository,
                         AccountNumberGenerator accountNumberGenerator) {
        this.clientRepository = clientRepository;
        this.changeLogRepository = changeLogRepository;
        this.accountNumberGenerator = accountNumberGenerator;
    }

    /**
     * Creates a new client with a unique account number
     * @param clientDTO the client data
     * @return the created client
     * @throws IllegalArgumentException if a client with the same national ID already exists
     */
    @Transactional
    public Client createClient(ClientDTO clientDTO) {
        // Check if client with same national ID already exists
        if (clientRepository.existsByNationalId(clientDTO.getNationalId())) {
            throw new IllegalArgumentException("A client with this national ID already exists");
        }

        // Create new client
        Client client = new Client();
        client.setName(clientDTO.getName());
        client.setNationalId(clientDTO.getNationalId());
        client.setDateOfBirth(clientDTO.getDateOfBirth());
        client.setClientType(clientDTO.getClientType());
        client.setPhoneNumber(clientDTO.getPhoneNumber());
        client.setAddress(clientDTO.getAddress());
        client.setPostalCode(clientDTO.getPostalCode());
        client.setAccountStatus(Client.AccountStatus.ACTIVE);

        // Generate unique account number
        String accountNumber = accountNumberGenerator.generateUniqueAccountNumber();
        client.setAccountNumber(accountNumber);

        // Set account creation and expiration dates
        LocalDateTime now = LocalDateTime.now();
        client.setAccountCreatedAt(now);
        client.setLastUsageDate(now);
        // Set expiration date to 5 years from now
        client.setAccountExpiresAt(now.plus(5, ChronoUnit.YEARS));

        return clientRepository.save(client);
    }

    /**
     * Updates a client's information
     * @param id the client ID
     * @param updateDTO the updated client data
     * @param updatedBy the user who made the update
     * @return the updated client
     * @throws IllegalArgumentException if the client is not found
     */
    @Transactional
    public Client updateClient(Long id, ClientUpdateDTO updateDTO, String updatedBy) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        // Log and update name if changed
        if (updateDTO.getName() != null && !updateDTO.getName().equals(client.getName())) {
            logChange(client, "name", client.getName(), updateDTO.getName(), updatedBy);
            client.setName(updateDTO.getName());
        }

        // Log and update date of birth if changed
        if (updateDTO.getDateOfBirth() != null && !updateDTO.getDateOfBirth().equals(client.getDateOfBirth())) {
            logChange(client, "dateOfBirth", client.getDateOfBirth().toString(), 
                     updateDTO.getDateOfBirth().toString(), updatedBy);
            client.setDateOfBirth(updateDTO.getDateOfBirth());
        }

        // Log and update client type if changed
        if (updateDTO.getClientType() != null && !updateDTO.getClientType().equals(client.getClientType())) {
            logChange(client, "clientType", client.getClientType().toString(), 
                     updateDTO.getClientType().toString(), updatedBy);
            client.setClientType(updateDTO.getClientType());
        }

        // Log and update phone number if changed
        if (updateDTO.getPhoneNumber() != null && !updateDTO.getPhoneNumber().equals(client.getPhoneNumber())) {
            logChange(client, "phoneNumber", client.getPhoneNumber(), 
                     updateDTO.getPhoneNumber(), updatedBy);
            client.setPhoneNumber(updateDTO.getPhoneNumber());
        }

        // Log and update address if changed
        if (updateDTO.getAddress() != null && !updateDTO.getAddress().equals(client.getAddress())) {
            logChange(client, "address", client.getAddress(), 
                     updateDTO.getAddress(), updatedBy);
            client.setAddress(updateDTO.getAddress());
        }

        // Log and update postal code if changed
        if (updateDTO.getPostalCode() != null && !updateDTO.getPostalCode().equals(client.getPostalCode())) {
            logChange(client, "postalCode", client.getPostalCode(), 
                     updateDTO.getPostalCode(), updatedBy);
            client.setPostalCode(updateDTO.getPostalCode());
        }

        return clientRepository.save(client);
    }

    /**
     * Retrieves account information by account number and updates last usage date
     * @param accountNumber the account number
     * @return the account information
     * @throws IllegalArgumentException if the account is not found
     */
    @Transactional
    public AccountInfoDTO getAccountInfo(String accountNumber) {
        Client client = clientRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        
        // Update last usage date
        client.setLastUsageDate(LocalDateTime.now());
        clientRepository.save(client);
        
        return AccountInfoDTO.fromClient(client);
    }

    /**
     * Retrieves account number by national ID
     * @param nationalId the national ID
     * @return the account number
     * @throws IllegalArgumentException if the client is not found
     */
    public String getAccountNumber(String nationalId) {
        Client client = clientRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));
        
        return client.getAccountNumber();
    }

    /**
     * Logs a change to a client's information
     * @param client the client
     * @param fieldName the name of the field that was changed
     * @param oldValue the old value
     * @param newValue the new value
     * @param changedBy the user who made the change
     */
    private void logChange(Client client, String fieldName, String oldValue, String newValue, String changedBy) {
        ClientChangeLog log = new ClientChangeLog(
            client.getId(),
            client.getName(),
            fieldName,
            oldValue,
            newValue,
            changedBy
        );
        changeLogRepository.save(log);
    }

    /**
     * Retrieves a client by their ID
     * @param id the client ID
     * @return an Optional containing the client if found
     */
    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    /**
     * Retrieves a client by their national ID
     * @param nationalId the national ID
     * @return an Optional containing the client if found
     */
    public Optional<Client> getClientByNationalId(String nationalId) {
        return clientRepository.findByNationalId(nationalId);
    }

    /**
     * Retrieves a client by their account number
     * @param accountNumber the account number
     * @return an Optional containing the client if found
     */
    public Optional<Client> getClientByAccountNumber(String accountNumber) {
        return clientRepository.findByAccountNumber(accountNumber);
    }

    /**
     * Retrieves all clients
     * @return a list of all clients
     */
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }
} 