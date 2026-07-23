package com.example.RewardService.service;


import com.example.RewardService.dto.CustomerRewardSummaryDTO;
import com.example.RewardService.dto.MonthlyRewardDTO;
import com.example.RewardService.exception.InvalidDateRangeException;
import com.example.RewardService.exception.ResourceNotFoundException;
import com.example.RewardService.model.Customer;
import com.example.RewardService.model.Transaction;
import com.example.RewardService.repository.TransactionRepository;
import com.example.RewardService.service.impl.RewardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RewardServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerService customerService;

    private RewardService rewardService;

    private Customer alice;

    @BeforeEach
    void setUp() {
        // Use the real calculator so the aggregation is checked against real math.
        rewardService = new RewardServiceImpl(transactionRepository, customerService, new RewardCalculator());
        alice = new Customer("Alice Anderson","abc@gamil.com","1234567","US street");
        alice.setId(1L);
    }

    @Test
    void aggregatesPointsPerMonthAndTotalForACustomer() {
        List<Transaction> transactions = List.of(
                new Transaction(alice, LocalDate.of(2024, 1, 15), new BigDecimal("120.00")), // 90
                new Transaction(alice, LocalDate.of(2024, 1, 22), new BigDecimal("45.00")),   // 0
                new Transaction(alice, LocalDate.of(2024, 2, 3), new BigDecimal("75.00"))     // 25
        );

        when(customerService.getCustomerEntityById(1L)).thenReturn(alice);
        when(transactionRepository.findByCustomerId(1L)).thenReturn(transactions);

        CustomerRewardSummaryDTO summary = rewardService.getRewardsForCustomerId(1L, null, null);

        assertThat(summary.getCustomerId()).isEqualTo(1L);
        assertThat(summary.getCustomerName()).isEqualTo("Alice Anderson");
        assertThat(summary.getTotalPoints()).isEqualTo(115);
        assertThat(summary.getMonthlyRewards())
                .extracting(MonthlyRewardDTO::getMonth, MonthlyRewardDTO::getPoints)
                .containsExactlyInAnyOrder(
                        tuple("2024-01", 90),
                        tuple("2024-02", 25)
                );
    }

    @Test
    void sumsMultipleTransactionsWithinTheSameMonth() {
        List<Transaction> transactions = List.of(
                new Transaction(alice, LocalDate.of(2024, 3, 1), new BigDecimal("120.00")), // 90
                new Transaction(alice, LocalDate.of(2024, 3, 20), new BigDecimal("120.00"))  // 90
        );

        when(customerService.getCustomerEntityById(1L)).thenReturn(alice);
        when(transactionRepository.findByCustomerId(1L)).thenReturn(transactions);

        CustomerRewardSummaryDTO summary = rewardService.getRewardsForCustomerId(1L, null, null);

        assertThat(summary.getMonthlyRewards()).hasSize(1);
        assertThat(summary.getMonthlyRewards().get(0).getMonth()).isEqualTo("2024-03");
        assertThat(summary.getMonthlyRewards().get(0).getPoints()).isEqualTo(180);
        assertThat(summary.getTotalPoints()).isEqualTo(180);
    }

    @Test
    void customerWithNoTransactionsHasZeroTotalAndNoMonths() {
        when(customerService.getCustomerEntityById(1L)).thenReturn(alice);
        when(transactionRepository.findByCustomerId(1L)).thenReturn(List.of());

        CustomerRewardSummaryDTO summary = rewardService.getRewardsForCustomerId(1L, null, null);

        assertThat(summary.getMonthlyRewards()).isEmpty();
        assertThat(summary.getTotalPoints()).isZero();
    }

    @Test
    void unknownCustomerIdPropagatesNotFound() {
        when(customerService.getCustomerEntityById(99L))
                .thenThrow(new ResourceNotFoundException("Customer not found with id: 99"));

        assertThatThrownBy(() -> rewardService.getRewardsForCustomerId(99L, null, null))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void rejectsStartDateAfterEndDate() {
        LocalDate start = LocalDate.of(2024, 3, 1);
        LocalDate end = LocalDate.of(2024, 1, 1);

        assertThatThrownBy(() -> rewardService.getRewardsForCustomerId(1L, start, end))
                .isInstanceOf(InvalidDateRangeException.class);
    }

    @Test
    void appliesDateRangeFilterWhenBothDatesProvided() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);

        List<Transaction> januaryOnly = List.of(
                new Transaction(alice, LocalDate.of(2024, 1, 15), new BigDecimal("120.00"))
        );

        when(customerService.getCustomerEntityById(1L)).thenReturn(alice);
        when(transactionRepository.findByCustomerIdAndTransactionDateBetween(1L, start, end))
                .thenReturn(januaryOnly);

        CustomerRewardSummaryDTO summary = rewardService.getRewardsForCustomerId(1L, start, end);

        assertThat(summary.getTotalPoints()).isEqualTo(90);
    }
}
