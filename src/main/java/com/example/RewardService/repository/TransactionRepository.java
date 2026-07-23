package com.example.RewardService.repository;


import com.example.RewardService.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByCustomerId(Long customerId);

    List<Transaction> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);

    List<Transaction> findByCustomerIdAndTransactionDateBetween(
            Long customerId, LocalDate startDate, LocalDate endDate);
}
