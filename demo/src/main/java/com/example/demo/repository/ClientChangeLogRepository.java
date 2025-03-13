package com.example.demo.repository;

import com.example.demo.model.ClientChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientChangeLogRepository extends JpaRepository<ClientChangeLog, Long> {
    
    List<ClientChangeLog> findByClientIdOrderByChangedAtDesc(Long clientId);
} 