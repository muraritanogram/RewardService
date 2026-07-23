package com.example.RewardService.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class RewardCalculatorTest {

    private final RewardCalculator calculator = new RewardCalculator();

    @Test
    void worksExampleFromSpec_120DollarPurchaseEarns90Points() {
        assertThat(calculator.calculatePoints(new BigDecimal("120.00"))).isEqualTo(90);
    }

    @ParameterizedTest(name = "amount={0} -> points={1}")
    @CsvSource({
            "0.00, 0",
            "10.00, 0",
            "49.99, 0",
            "50.00, 0",     // exactly at the lower threshold earns nothing
            "50.01, 0",     // rounds to 0 points for one cent over
            "75.00, 25",
            "99.99, 50",    // 49.99 dollars over $50 rounds HALF_UP to 50 points
            "100.00, 50",   // exactly at the upper threshold: flat 50
            "100.01, 50",
            "120.00, 90",
            "150.00, 150",
            "200.00, 250",
            "500.00, 850"
    })
    void calculatesPointsAcrossAllTiers(String amount, int expectedPoints) {
        assertThat(calculator.calculatePoints(new BigDecimal(amount))).isEqualTo(expectedPoints);
    }

    @Test
    void nullAmountEarnsZeroPoints() {
        assertThat(calculator.calculatePoints(null)).isEqualTo(0);
    }

    @Test
    void negativeAmountEarnsZeroPoints() {
        assertThat(calculator.calculatePoints(new BigDecimal("-10.00"))).isEqualTo(0);
    }

    @Test
    void zeroAmountEarnsZeroPoints() {
        assertThat(calculator.calculatePoints(BigDecimal.ZERO)).isEqualTo(0);
    }
}
