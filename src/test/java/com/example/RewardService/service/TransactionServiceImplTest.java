package com.example.RewardService.service;


import com.example.RewardService.dto.TransactionRequest;
import com.example.RewardService.dto.TransactionResponse;
import com.example.RewardService.exception.InvalidDateRangeException;
import com.example.RewardService.model.Customer;
import com.example.RewardService.model.Transaction;
import com.example.RewardService.repository.TransactionRepository;
import com.example.RewardService.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerService customerService;

    private TransactionService transactionService;

    private Customer alice;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionServiceImpl(transactionRepository, customerService, new RewardCalculator());
        alice = new Customer("Alice Anderson");
        alice.setId(1L);
    }

    @Test
    void createTransactionPersistsAndReturnsCalculatedPoints() {
        TransactionRequest request = new TransactionRequest(1L, LocalDate.of(2024, 1, 15), new BigDecimal("120.00"));
        Transaction saved = new Transaction(alice, request.getTransactionDate(), request.getAmount());
        saved.setId(10L);

        when(customerService.getCustomerEntityById(1L)).thenReturn(alice);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(saved);

        TransactionResponse response = transactionService.createTransaction(request);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getCustomerId()).isEqualTo(1L);
        assertThat(response.getCustomerName()).isEqualTo("Alice Anderson");
        assertThat(response.getPointsEarned()).isEqualTo(90);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void getTransactionsFiltersByCustomerAndDateRange() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);
        Transaction transaction = new Transaction(alice, LocalDate.of(2024, 1, 15), new BigDecimal("75.00"));
        transaction.setId(5L);

        when(transactionRepository.findByCustomerIdAndTransactionDateBetween(1L, start, end))
                .thenReturn(List.of(transaction));

        List<TransactionResponse> results = transactionService.getTransactions(1L, start, end);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getPointsEarned()).isEqualTo(25);
    }

    @Test
    void rejectsInvertedDateRange() {
        LocalDate start = LocalDate.of(2024, 3, 1);
        LocalDate end = LocalDate.of(2024, 1, 1);

        assertThatThrownBy(() -> transactionService.getTransactions(null, start, end))
                .isInstanceOf(InvalidDateRangeException.class);
    }
}
