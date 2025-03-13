package com.example.demo.controller;

import com.example.demo.model.ApiResponse;
import com.example.demo.model.Client;
import com.example.demo.model.ClientDTO;
import com.example.demo.model.ClientUpdateDTO;
import com.example.demo.model.AccountInfoDTO;
import com.example.demo.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Creates a new client
     * @param clientDTO the client data
     * @return the created client with account number
     */
    @PostMapping("/create-account")
    public ResponseEntity<ApiResponse<Client>> createClient(@Valid @RequestBody ClientDTO clientDTO) {
        try {
            Client createdClient = clientService.createClient(clientDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Client account created successfully", createdClient));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Updates a client's information
     * @param id the client ID
     * @param updateDTO the updated client data
     * @return the updated client
     */
    @PutMapping("/update-account/{id}")
    public ResponseEntity<ApiResponse<Client>> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientUpdateDTO updateDTO) {
        try {
            // In a real application, you would get the username from the authenticated user
            String updatedBy = "system"; // Placeholder for the authenticated user
            
            Client updatedClient = clientService.updateClient(id, updateDTO, updatedBy);
            return ResponseEntity.ok(ApiResponse.success("Client information updated successfully", updatedClient));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Retrieves a client by ID
     * @param id the client ID
     * @return the client if found
     */
    @GetMapping("/get-account-{id}")
    public ResponseEntity<ApiResponse<Client>> getClientById(@PathVariable Long id) {
        Optional<Client> client = clientService.getClientById(id);
        return client.map(c -> ResponseEntity.ok(ApiResponse.success(c)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Client not found")));
    }

    /**
     * Retrieves a client by national ID
     * @param nationalId the national ID
     * @return the client if found
     */
    @GetMapping("/get-account-by-national-id-{nationalId}")
    public ResponseEntity<ApiResponse<Client>> getClientByNationalId(@PathVariable String nationalId) {
        Optional<Client> client = clientService.getClientByNationalId(nationalId);
        return client.map(c -> ResponseEntity.ok(ApiResponse.success(c)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Client not found")));
    }

    /**
     * Retrieves a client by account number
     * @param accountNumber the account number
     * @return the client if found
     */
    @GetMapping("/get-account-by-account-number-{accountNumber}")
    public ResponseEntity<ApiResponse<Client>> getClientByAccountNumber(@PathVariable String accountNumber) {
        Optional<Client> client = clientService.getClientByAccountNumber(accountNumber);
        return client.map(c -> ResponseEntity.ok(ApiResponse.success(c)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Client not found")));
    }

    /**
     * Retrieves all clients
     * @return a list of all clients
     */
    @GetMapping("/get-all-accounts")
    public ResponseEntity<ApiResponse<List<Client>>> getAllClients() {
        List<Client> clients = clientService.getAllClients();
        return ResponseEntity.ok(ApiResponse.success(clients));
    }

    /**
     * Retrieves account information
     * @param accountNumber the account number
     * @return the account information
     */
    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<AccountInfoDTO> getAccountInfo(@PathVariable String accountNumber) {
        AccountInfoDTO accountInfo = clientService.getAccountInfo(accountNumber);
        return ResponseEntity.ok(accountInfo);
    }

    /**
     * Retrieves account number by national ID
     * @param nationalId the national ID
     * @return the account number
     */
    @GetMapping("/national-id/{nationalId}/account")
    public ResponseEntity<String> getAccountNumber(@PathVariable String nationalId) {
        String accountNumber = clientService.getAccountNumber(nationalId);
        return ResponseEntity.ok(accountNumber);
    }

    /**
     * Handles validation errors
     * @param ex the validation exception
     * @return a map of field errors
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ApiResponse.error("Validation failed");
    }
} 