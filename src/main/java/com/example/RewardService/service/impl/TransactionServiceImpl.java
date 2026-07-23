package com.example.RewardService.service.impl;


import com.example.RewardService.dto.TransactionRequest;
import com.example.RewardService.dto.TransactionResponse;
import com.example.RewardService.model.Customer;
import com.example.RewardService.model.Transaction;
import com.example.RewardService.repository.TransactionRepository;
import com.example.RewardService.service.CustomerService;
import com.example.RewardService.service.DateRangeValidator;
import com.example.RewardService.service.RewardCalculator;
import com.example.RewardService.service.TransactionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerService customerService;
    private final RewardCalculator rewardCalculator;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                   CustomerService customerService,
                                   RewardCalculator rewardCalculator) {
        this.transactionRepository = transactionRepository;
        this.customerService = customerService;
        this.rewardCalculator = rewardCalculator;
    }

    @Override
    public TransactionResponse createTransaction(TransactionRequest request) {
        Customer customer = customerService.getCustomerEntityById(request.getCustomerId());

        Transaction transaction = new Transaction(customer, request.getTransactionDate(), request.getAmount());
        Transaction saved = transactionRepository.save(transaction);

        return toResponse(saved);
    }

    @Override
    public List<TransactionResponse> getTransactions(Long customerId, LocalDate startDate, LocalDate endDate) {
        DateRangeValidator.validate(startDate, endDate);


        List<Transaction> transactions;
        if (customerId != null && startDate != null && endDate != null) {
            transactions = transactionRepository.findByCustomerIdAndTransactionDateBetween(customerId, startDate, endDate);
        } else if (customerId != null) {
            transactions = transactionRepository.findByCustomerId(customerId);
        } else if (startDate != null && endDate != null) {
            transactions = transactionRepository.findByTransactionDateBetween(startDate, endDate);
        } else {
            transactions = transactionRepository.findAll();
        }

        return transactions.stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate))
                .map(this::toResponse)
                .toList();
    }

    private TransactionResponse toResponse(Transaction transaction) {
        int points = rewardCalculator.calculatePoints(transaction.getAmount());
        return new TransactionResponse(
                transaction.getId(),
                transaction.getCustomer().getId(),
                transaction.getCustomer().getName(),
                transaction.getTransactionDate(),
                transaction.getAmount(),
                points
        );
    }
}
