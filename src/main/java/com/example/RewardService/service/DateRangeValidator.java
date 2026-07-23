package com.example.RewardService.service;



import com.example.RewardService.exception.InvalidDateRangeException;

import java.time.LocalDate;

public final class DateRangeValidator {

    private DateRangeValidator() {
    }

    public static void validate(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("startDate must not be after endDate");
        }
    }
}
