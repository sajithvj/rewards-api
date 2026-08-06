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
            new Transaction("C001", "Alice Nguyen", new BigDecimal("120.00"), LocalDate.of(2026, 4, 3)),
            new Transaction("C001", "Alice Nguyen", new BigDecimal("75.50"), LocalDate.of(2026, 4, 20)),
            new Transaction("C001", "Alice Nguyen", new BigDecimal("45.00"), LocalDate.of(2026, 5, 8)),
            new Transaction("C001", "Alice Nguyen", new BigDecimal("200.00"), LocalDate.of(2026, 5, 22)),
            new Transaction("C001", "Alice Nguyen", new BigDecimal("99.99"), LocalDate.of(2026, 6, 14)),

            // Ben: no purchases in one of the three months
            new Transaction("C002", "Ben Carter", new BigDecimal("50.00"), LocalDate.of(2026, 4, 5)),
            new Transaction("C002", "Ben Carter", new BigDecimal("150.75"), LocalDate.of(2026, 6, 2)),

            // Priya: consistently high spender
            new Transaction("C003", "Priya Sharma", new BigDecimal("310.00"), LocalDate.of(2026, 4, 11)),
            new Transaction("C003", "Priya Sharma", new BigDecimal("260.40"), LocalDate.of(2026, 5, 17)),
            new Transaction("C003", "Priya Sharma", new BigDecimal("180.00"), LocalDate.of(2026, 6, 29)),

            // David: only ever spends under $50, so he never earns points
            new Transaction("C004", "David Kim", new BigDecimal("30.00"), LocalDate.of(2026, 4, 9)),
            new Transaction("C004", "David Kim", new BigDecimal("49.99"), LocalDate.of(2026, 5, 30))
    );

    public List<Transaction> findAll() {
        return transactions;
    }
}
