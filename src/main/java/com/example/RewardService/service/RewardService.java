package com.example.RewardService.service;



import com.example.RewardService.dto.CustomerRewardSummaryDTO;

import java.time.LocalDate;
import java.util.List;

public interface RewardService {

    List<CustomerRewardSummaryDTO> getRewardsForAllCustomers(LocalDate startDate, LocalDate endDate);

    CustomerRewardSummaryDTO getRewardsForCustomerId(Long customerId, LocalDate startDate, LocalDate endDate);

    CustomerRewardSummaryDTO getRewardsForCustomerName(String customerName, LocalDate startDate, LocalDate endDate);
}
