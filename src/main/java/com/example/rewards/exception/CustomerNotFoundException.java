package com.example.rewards.exception;

import java.time.LocalDate;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(LocalDate startDate, LocalDate endDate) {
        super("No transactions found for the given date range: " + startDate + " to " + endDate);
    }
}
