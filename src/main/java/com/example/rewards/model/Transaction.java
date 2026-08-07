package com.example.rewards.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A single recorded purchase made by a customer.
 */
public record Transaction(String transactionId, String customerId, String customerName, BigDecimal amount,
                          LocalDate transactionDate) {

    public Transaction(String transactionId, String customerId, String customerName, BigDecimal amount, LocalDate transactionDate) {
        this.transactionId = Objects.requireNonNull(transactionId, "transactionId is required");
        this.customerId = Objects.requireNonNull(customerId, "customerId is required");
        this.customerName = Objects.requireNonNull(customerName, "customerName is required");
        this.amount = Objects.requireNonNull(amount, "amount is required");
        this.transactionDate = Objects.requireNonNull(transactionDate, "transactionDate is required");
    }
}
