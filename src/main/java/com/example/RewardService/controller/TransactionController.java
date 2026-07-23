package com.example.RewardService.controller;


import com.example.RewardService.dto.TransactionRequest;
import com.example.RewardService.dto.TransactionResponse;
import com.example.RewardService.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    public static final Logger LOGGER= LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Record a new purchase transaction for a customer")
    @PostMapping("/createTransaction")
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransactionRequest request) {

        LOGGER.info(
                "Received request to create transaction. CustomerId={}, Amount={}, TransactionDate={}",
                request.getCustomerId(),
                request.getAmount(),
                request.getTransactionDate());

        TransactionResponse response = transactionService.createTransaction(request);

        LOGGER.info(
                "Transaction created successfully. TransactionId={}, CustomerId={}",
                response.getId(),
                response.getCustomerId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }



    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @Parameter(description = "Filter to a single customer's transactions")
            @RequestParam(required = false) Long customerId,
            @Parameter(description = "Inclusive start date, e.g. 2024-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Inclusive end date, e.g. 2024-03-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
    {

        LOGGER.info(
                "Received request to fetch transactions. CustomerId={}, StartDate={}, EndDate={}",
                customerId,
                startDate,
                endDate);

        List<TransactionResponse> transactions =
                transactionService.getTransactions(customerId, startDate, endDate);

        LOGGER.info(
                "Successfully fetched {} transaction(s)",
                transactions.size());

        return ResponseEntity.ok(transactions);

    }
}
