package com.example.demo.repository;

import com.example.demo.model.TransferTracking;
import com.example.demo.model.TransferType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TransferTrackingRepository extends JpaRepository<TransferTracking, String> {
    Optional<TransferTracking> findByTrackingCode(String trackingCode);

    @Query("SELECT t FROM TransferTracking t " +
           "WHERE (:accountNumber IS NULL OR t.senderAccountNumber = :accountNumber OR t.receiverAccountNumber = :accountNumber) " +
           "AND (:type IS NULL OR t.type = :type) " +
           "AND (:originAccount IS NULL OR t.senderAccountNumber = :originAccount) " +
           "AND (:destinationAccount IS NULL OR t.receiverAccountNumber = :destinationAccount) " +
           "AND (:minAmount IS NULL OR t.amount >= :minAmount) " +
           "AND (:maxAmount IS NULL OR t.amount <= :maxAmount) " +
           "AND (:startDate IS NULL OR t.requestDate >= :startDate) " +
           "AND (:endDate IS NULL OR t.requestDate <= :endDate) " +
           "ORDER BY t.requestDate DESC")
    Page<TransferTracking> findAllWithFilters(
            @Param("accountNumber") String accountNumber,
            @Param("type") TransferType type,
            @Param("originAccount") String originAccount,
            @Param("destinationAccount") String destinationAccount,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT t FROM TransferTracking t " +
           "WHERE t.senderAccountNumber = :accountNumber " +
           "AND t.type = :type " +
           "ORDER BY t.requestDate DESC")
    Page<TransferTracking> findLastTransactionsByAccountAndType(
            @Param("accountNumber") String accountNumber,
            @Param("type") TransferType type,
            Pageable pageable
    );
} 