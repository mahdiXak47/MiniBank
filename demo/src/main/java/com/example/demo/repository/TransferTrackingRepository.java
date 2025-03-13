package com.example.demo.repository;

import com.example.demo.model.TransferTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransferTrackingRepository extends JpaRepository<TransferTracking, String> {
    Optional<TransferTracking> findByTrackingCode(String trackingCode);
} 