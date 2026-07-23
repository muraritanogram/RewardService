package com.example.RewardService.service;



import com.example.RewardService.dto.TransactionRequest;
import com.example.RewardService.dto.TransactionResponse;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

    TransactionResponse createTransaction(TransactionRequest request);

    List<TransactionResponse> getTransactions(Long customerId, LocalDate startDate, LocalDate endDate);
}
