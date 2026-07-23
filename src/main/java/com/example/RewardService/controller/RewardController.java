package com.example.RewardService.controller;

import com.example.RewardService.dto.CustomerRewardSummaryDTO;
import com.example.RewardService.service.RewardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    private static final Logger LOGGER =LoggerFactory.getLogger(RewardController.class);
    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }


    @GetMapping("/getRewardsForAllCustomers")
    public ResponseEntity<List<CustomerRewardSummaryDTO>> getAllRewards(
            @Parameter(description = "Inclusive start date, e.g. 2024-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Inclusive end date, e.g. 2024-03-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
    {
        LOGGER.info("Received request to fetch reward summary for all customers. StartDate={}, EndDate={}",
                startDate, endDate);

        List<CustomerRewardSummaryDTO> rewards =
                rewardService.getRewardsForAllCustomers(startDate, endDate);

        LOGGER.info("Successfully fetched reward summary for {} customers",
                rewards.size());

        return ResponseEntity.ok(rewards);
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<CustomerRewardSummaryDTO> getRewardsByCustomerId(
            @PathVariable Long customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        LOGGER.info("Received request to fetch rewards for CustomerId={}, StartDate={}, EndDate={}",
                customerId, startDate, endDate);

        CustomerRewardSummaryDTO response =
                rewardService.getRewardsForCustomerId(customerId, startDate, endDate);

        LOGGER.info("Successfully fetched rewards for CustomerId={}", customerId);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/customers/name/{customerName}")
    public ResponseEntity<CustomerRewardSummaryDTO> getRewardsByCustomerName(
            @PathVariable String customerName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        LOGGER.info("Received request to fetch rewards for CustomerName='{}', StartDate={}, EndDate={}",
                customerName, startDate, endDate);

        CustomerRewardSummaryDTO response =
                rewardService.getRewardsForCustomerName(customerName, startDate, endDate);

        LOGGER.info("Successfully fetched rewards for CustomerName='{}'", customerName);

        return ResponseEntity.ok(response);
    }
}
