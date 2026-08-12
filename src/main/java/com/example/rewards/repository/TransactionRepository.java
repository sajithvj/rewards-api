package com.example.rewards.repository;

import com.example.rewards.model.Transaction;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * In-memory stand-in for a real data store. Seeded with a made-up
 * three-month transaction history covering a few customers, chosen to
 * exercise all three reward tiers (under $50, $50-$100, over $100).
 */
@Repository
public class TransactionRepository {

    private final List<Transaction> transactions = List.of(
            // Alice: mix of small, medium, and large purchases across 3 months
            new Transaction("T0001", "C001", "Alice Job", new BigDecimal("120.00"), LocalDate.of(2026, 5, 3)),
            new Transaction("T0002", "C001", "Alice Job", new BigDecimal("75.50"), LocalDate.of(2026, 5, 20)),
            new Transaction("T0003", "C001", "Alice Job", new BigDecimal("45.00"), LocalDate.of(2026, 6, 8)),
            new Transaction("T0004", "C001", "Alice Job", new BigDecimal("200.00"), LocalDate.of(2026, 6, 22)),
            new Transaction("T0005", "C001", "Alice Job", new BigDecimal("99.99"), LocalDate.of(2026, 7, 14)),

            // Ben: no purchases in one of the three months
            new Transaction("T0006", "C002", "Sonu Venu", new BigDecimal("50.00"), LocalDate.of(2026, 5, 5)),
            new Transaction("T0007", "C002", "Sonu Venu", new BigDecimal("150.75"), LocalDate.of(2026, 7, 2)),

            // Priya: consistently high spender
            new Transaction("T0008", "C003", "Priya Sharma", new BigDecimal("310.00"), LocalDate.of(2026, 5, 11)),
            new Transaction("T0009", "C003", "Priya Sharma", new BigDecimal("260.40"), LocalDate.of(2026, 6, 17)),
            new Transaction("T00010", "C003", "Priya Sharma", new BigDecimal("180.00"), LocalDate.of(2026, 7, 29)),

            // David: only ever spends under $50, so he never earns points
            new Transaction("T00011", "C004", "David John", new BigDecimal("30.00"), LocalDate.of(2026, 5, 9)),
            new Transaction("T00012", "C004", "David John", new BigDecimal("49.99"), LocalDate.of(2026, 6, 30)),

            new Transaction("T00013", "C005", "Nirmal Xavier", new BigDecimal("120.00"), LocalDate.of(2026, 6, 9)),
            new Transaction("T00014", "C005", "Nirmal Xavier", new BigDecimal("60.00"), LocalDate.of(2026, 7, 9)),
            new Transaction("T00015", "C005", "Nirmal Xavier", new BigDecimal("52.00"), LocalDate.of(2026, 8, 9))

    );

    public List<Transaction> findAll() {
        return transactions;
    }

    public List<Transaction> findByCustomerIdAndTransactionDateBetween(String customerId, LocalDate startDate, LocalDate endDate) {
        return transactions.stream()
                .filter(t -> t.customerId().equals(customerId))
                .filter(t -> !t.transactionDate().isBefore(startDate) && !t.transactionDate().isAfter(endDate))
                .toList();
    }
    public List<Transaction> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate) {
        return transactions.stream()
                .filter(t -> !t.transactionDate().isBefore(startDate) && !t.transactionDate().isAfter(endDate))
                .toList();
    }
}
