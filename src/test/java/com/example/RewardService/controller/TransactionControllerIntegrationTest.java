package com.example.RewardService.controller;

import com.example.RewardService.dto.TransactionRequest;
import com.example.RewardService.model.Customer;
import com.example.RewardService.model.Transaction;
import com.example.RewardService.repository.CustomerRepository;
import com.example.RewardService.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
//    private ObjectMapper objectMapper=new ObjectMapper();

    private Customer alice;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        customerRepository.deleteAll();
        alice = customerRepository.save(new Customer("Alice Anderson","abc@gamil.com","1234567","US street"));
    }

    @Test
    void createTransaction_returns201AndCalculatedPoints() throws Exception {
        TransactionRequest request = new TransactionRequest(alice.getId(), LocalDate.of(2024, 1, 15), new BigDecimal("120.00"));

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(alice.getId()))
                .andExpect(jsonPath("$.pointsEarned").value(90));
    }

    @Test
    void createTransaction_missingAmount_returns400WithFieldError() throws Exception {
        String invalidJson = """
                {
                  "customerId": %d,
                  "transactionDate": "2024-01-15"
                }
                """.formatted(alice.getId());

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.amount").exists());
    }

    @Test
    void createTransaction_negativeAmount_returns400() throws Exception {
        String invalidJson = """
                {
                  "customerId": %d,
                  "transactionDate": "2024-01-15",
                  "amount": -5.00
                }
                """.formatted(alice.getId());

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.amount").exists());
    }

    @Test
    void createTransaction_futureDate_returns400() throws Exception {
        TransactionRequest request = new TransactionRequest(
                alice.getId(), LocalDate.now().plusDays(5), new BigDecimal("60.00"));

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.transactionDate").exists());
    }

    @Test
    void createTransaction_unknownCustomer_returns404() throws Exception {
        TransactionRequest request = new TransactionRequest(999999L, LocalDate.of(2024, 1, 15), new BigDecimal("60.00"));

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTransactions_filtersByCustomerIdAndDateRange() throws Exception {
        transactionRepository.save(new Transaction(alice, LocalDate.of(2024, 1, 15), new BigDecimal("120.00")));
        transactionRepository.save(new Transaction(alice, LocalDate.of(2024, 2, 10), new BigDecimal("80.00")));

        mockMvc.perform(get("/api/v1/transactions")
                        .param("customerId", String.valueOf(alice.getId()))
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].pointsEarned").value(90));
    }

    @Test
    void getTransactions_invertedDateRange_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/transactions")
                        .param("startDate", "2024-03-01")
                        .param("endDate", "2024-01-01"))
                .andExpect(status().isBadRequest());
    }
}
