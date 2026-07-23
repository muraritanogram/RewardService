package com.example.RewardService.service.impl;

import com.example.RewardService.dto.CustomerRewardSummaryDTO;
import com.example.RewardService.dto.MonthlyRewardDTO;
import com.example.RewardService.model.Customer;
import com.example.RewardService.model.Transaction;
import com.example.RewardService.repository.TransactionRepository;
import com.example.RewardService.service.CustomerService;
import com.example.RewardService.service.DateRangeValidator;
import com.example.RewardService.service.RewardCalculator;
import com.example.RewardService.service.RewardService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class RewardServiceImpl implements RewardService {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private final TransactionRepository transactionRepository;
    private final CustomerService customerService;
    private final RewardCalculator rewardCalculator;

    public RewardServiceImpl(TransactionRepository transactionRepository,
                              CustomerService customerService,
                              RewardCalculator rewardCalculator) {
        this.transactionRepository = transactionRepository;
        this.customerService = customerService;
        this.rewardCalculator = rewardCalculator;
    }

    @Override
    public List<CustomerRewardSummaryDTO> getRewardsForAllCustomers(LocalDate startDate, LocalDate endDate) {
        DateRangeValidator.validate(startDate, endDate);

        // last 3 months window
        if(startDate==null && endDate==null)
        {
            startDate = LocalDate.now().minusMonths(3);
            endDate = LocalDate.now();
        }

        List<Transaction> transactions = fetchTransactions(null, startDate, endDate);

        Map<Customer, List<Transaction>> byCustomer = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getCustomer));

        return byCustomer.entrySet().stream()
                .map(entry -> buildSummary(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(CustomerRewardSummaryDTO::getCustomerName))
                .toList();
    }

    @Override
    public CustomerRewardSummaryDTO getRewardsForCustomerId(Long customerId, LocalDate startDate, LocalDate endDate) {
        DateRangeValidator.validate(startDate, endDate);

        Customer customer = customerService.getCustomerEntityById(customerId);
        List<Transaction> transactions = fetchTransactions(customerId, startDate, endDate);

        return buildSummary(customer, transactions);
    }

    @Override
    public CustomerRewardSummaryDTO getRewardsForCustomerName(String customerName, LocalDate startDate, LocalDate endDate) {
        DateRangeValidator.validate(startDate, endDate);

        Customer customer = customerService.getCustomerEntityByName(customerName);
        List<Transaction> transactions = fetchTransactions(customer.getId(), startDate, endDate);

        return buildSummary(customer, transactions);
    }

    private List<Transaction> fetchTransactions(Long customerId, LocalDate startDate, LocalDate endDate) {
        if (customerId != null && startDate != null && endDate != null) {
            return transactionRepository.findByCustomerIdAndTransactionDateBetween(customerId, startDate, endDate);
        } else if (customerId != null) {
            return transactionRepository.findByCustomerId(customerId);
        } else if (startDate != null && endDate != null) {
            return transactionRepository.findByTransactionDateBetween(startDate, endDate);
        }
        return transactionRepository.findAll();
    }


    private CustomerRewardSummaryDTO buildSummary(Customer customer, List<Transaction> transactions) {
        Map<YearMonth, Integer> pointsByMonth = new TreeMap<>();

        for (Transaction transaction : transactions) {
            YearMonth month = YearMonth.from(transaction.getTransactionDate());
            int points = rewardCalculator.calculatePoints(transaction.getAmount());
            pointsByMonth.merge(month, points, Integer::sum);
        }

        List<MonthlyRewardDTO> monthlyRewards = pointsByMonth.entrySet().stream()
                .map(e -> new MonthlyRewardDTO(e.getKey().format(MONTH_FORMATTER), e.getValue()))
                .toList();

        int totalPoints = pointsByMonth.values().stream().mapToInt(Integer::intValue).sum();

        return new CustomerRewardSummaryDTO(customer.getId(), customer.getName(), monthlyRewards, totalPoints);
    }
}
