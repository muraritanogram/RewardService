package com.example.RewardService.dto;

import java.math.BigDecimal;
import java.time.LocalDate;


public class TransactionResponse {

    private Long id;
    private Long customerId;
    private String customerName;
    private LocalDate transactionDate;
    private BigDecimal amount;
    private int pointsEarned;

    public TransactionResponse() {
    }

    public TransactionResponse(Long id, Long customerId, String customerName,
                                LocalDate transactionDate, BigDecimal amount, int pointsEarned) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.pointsEarned = pointsEarned;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }
}
