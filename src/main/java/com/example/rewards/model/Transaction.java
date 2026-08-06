package com.example.rewards.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A single recorded purchase made by a customer.
 */
public class Transaction {

    private final String customerId;
    private final String customerName;
    private final BigDecimal amount;
    private final LocalDate transactionDate;

    public Transaction(String customerId, String customerName, BigDecimal amount, LocalDate transactionDate) {
        this.customerId = Objects.requireNonNull(customerId, "customerId is required");
        this.customerName = Objects.requireNonNull(customerName, "customerName is required");
        this.amount = Objects.requireNonNull(amount, "amount is required");
        this.transactionDate = Objects.requireNonNull(transactionDate, "transactionDate is required");
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }
}
