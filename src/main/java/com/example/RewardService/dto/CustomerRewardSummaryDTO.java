package com.example.RewardService.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Full reward summary for one customer: points broken down per month,
 * plus the total across the requested/aggregated period.
 */
public class CustomerRewardSummaryDTO {

    private Long customerId;
    private String customerName;
    private List<MonthlyRewardDTO> monthlyRewards = new ArrayList<>();
    private int totalPoints;

    public CustomerRewardSummaryDTO() {
    }

    public CustomerRewardSummaryDTO(Long customerId, String customerName,
                                     List<MonthlyRewardDTO> monthlyRewards, int totalPoints) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.monthlyRewards = monthlyRewards;
        this.totalPoints = totalPoints;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public List<MonthlyRewardDTO> getMonthlyRewards() {
        return monthlyRewards;
    }

    public void setMonthlyRewards(List<MonthlyRewardDTO> monthlyRewards) {
        this.monthlyRewards = monthlyRewards;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }
}
