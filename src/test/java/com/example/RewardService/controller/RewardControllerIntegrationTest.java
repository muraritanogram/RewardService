package com.example.RewardService.controller;

import com.example.RewardService.model.Customer;
import com.example.RewardService.model.Transaction;
import com.example.RewardService.repository.CustomerRepository;
import com.example.RewardService.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RewardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;



    private Customer alice;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        customerRepository.deleteAll();

        alice = customerRepository.save(new Customer("Alice Anderson","abc@gamil.com","1234567","US street"));

        transactionRepository.save(new Transaction(alice, LocalDate.of(2024, 1, 15), new BigDecimal("120.00"))); // 90
        transactionRepository.save(new Transaction(alice, LocalDate.of(2024, 1, 22), new BigDecimal("45.00")));  // 0
        transactionRepository.save(new Transaction(alice, LocalDate.of(2024, 2, 3), new BigDecimal("75.00")));   // 25
    }

    @Test
    void getRewardsByCustomerId_returnsMonthlyBreakdownAndTotal() throws Exception {
        mockMvc.perform(get("/api/v1/rewards/customerId/{customerId}", alice.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(alice.getId()))
                .andExpect(jsonPath("$.customerName").value("Alice Anderson"))
                .andExpect(jsonPath("$.totalPoints").value(115))
                .andExpect(jsonPath("$.monthlyRewards", hasSize(2)))
                .andExpect(jsonPath("$.monthlyRewards[?(@.month=='2024-01')].points").value(90))
                .andExpect(jsonPath("$.monthlyRewards[?(@.month=='2024-02')].points").value(25));
    }

    @Test
    void getRewardsByCustomerName_isCaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/v1/rewards/customerName/{customerName}", "alice anderson"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPoints").value(115));
    }

    @Test
    void getRewardsByCustomerId_withDateRangeFilter_onlyIncludesMatchingMonth() throws Exception {
        mockMvc.perform(get("/api/v1/rewards/customerId/{customerId}", alice.getId())
                        .param("startDate", "2024-02-01")
                        .param("endDate", "2024-02-29"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPoints").value(25))
                .andExpect(jsonPath("$.monthlyRewards", hasSize(1)))
                .andExpect(jsonPath("$.monthlyRewards[0].month").value("2024-02"));
    }

    @Test
    void getRewardsByCustomerId_invertedDateRange_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/rewards/customerId/{customerId}", alice.getId())
                        .param("startDate", "2024-03-01")
                        .param("endDate", "2024-01-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void getRewardsByUnknownCustomerId_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/rewards/customerId/{customerId}", 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }


}
