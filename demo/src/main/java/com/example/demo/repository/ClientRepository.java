package com.example.demo.repository;

import com.example.demo.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    boolean existsByNationalId(String nationalId);
    
    boolean existsByAccountNumber(String accountNumber);
    
    Optional<Client> findByNationalId(String nationalId);
    
    Optional<Client> findByAccountNumber(String accountNumber);
} 