package com.example.RewardService.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;


@Component
public class RewardCalculator {

    private static final BigDecimal FIFTY = BigDecimal.valueOf(50);
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal TWO = BigDecimal.valueOf(2);


    public int calculatePoints(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            return 0;
        }

        BigDecimal points = BigDecimal.ZERO;
        BigDecimal remaining = amount;

        if (remaining.compareTo(HUNDRED) > 0) {
            BigDecimal amountOverHundred = remaining.subtract(HUNDRED);
            points = points.add(amountOverHundred.multiply(TWO));
            remaining = HUNDRED;
        }

        if (remaining.compareTo(FIFTY) > 0) {
            BigDecimal amountBetweenFiftyAndHundred = remaining.subtract(FIFTY);
            points = points.add(amountBetweenFiftyAndHundred);
        }

        return points.setScale(0, RoundingMode.HALF_UP).intValue();
    }
}
