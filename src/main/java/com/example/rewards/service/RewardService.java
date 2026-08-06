package com.example.rewards.service;

import com.example.rewards.dto.CustomerRewardSummary;
import com.example.rewards.dto.MonthlyReward;
import com.example.rewards.model.Transaction;
import com.example.rewards.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class RewardService {

    private static final BigDecimal TIER_2_THRESHOLD = new BigDecimal("100");
    private static final BigDecimal TIER_1_THRESHOLD = new BigDecimal("50");

    private final TransactionRepository transactionRepository;

    public RewardService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Points earned on a single transaction:
     * 2 points per dollar spent over $100, plus 1 point per dollar spent
     * between $50 and $100.
     * <p>
     * e.g. $120 -> 2x$20 (over 100) + 1x$50 (50-100 band) = 90 points.
     * Fractional dollars are truncated per-transaction (standard rewards
     * practice), matching the worked example in the spec.
     */
    public int calculatePoints(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            return 0;
        }

        BigDecimal remaining = amount;
        BigDecimal points = BigDecimal.ZERO;

        if (remaining.compareTo(TIER_2_THRESHOLD) > 0) {
            BigDecimal overHundred = remaining.subtract(TIER_2_THRESHOLD);
            points = points.add(overHundred.multiply(BigDecimal.valueOf(2)));
            remaining = TIER_2_THRESHOLD;
        }

        if (remaining.compareTo(TIER_1_THRESHOLD) > 0) {
            BigDecimal fiftyToHundred = remaining.subtract(TIER_1_THRESHOLD);
            points = points.add(fiftyToHundred);
        }

        return points.intValue();
    }

    /**
     * Builds a per-customer summary of reward points, broken down by
     * calendar month, plus the total across all months on record.
     */
    public List<CustomerRewardSummary> getRewardSummaries() {
        Map<String, List<Transaction>> byCustomer = transactionRepository.findAll().stream()
                .collect(Collectors.groupingBy(Transaction::getCustomerId));

        return byCustomer.entrySet().stream()
                .map(entry -> buildSummary(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(CustomerRewardSummary::getCustomerName))
                .collect(Collectors.toList());
    }

    private CustomerRewardSummary buildSummary(String customerId, List<Transaction> customerTransactions) {
        String customerName = customerTransactions.get(0).getCustomerName();

        // TreeMap keeps months in chronological order in the response.
        Map<YearMonth, Integer> pointsByMonth = new TreeMap<>();
        for (Transaction t : customerTransactions) {
            YearMonth month = YearMonth.from(t.getTransactionDate());
            int points = calculatePoints(t.getAmount());
            pointsByMonth.merge(month, points, Integer::sum);
        }

        List<MonthlyReward> monthlyRewards = pointsByMonth.entrySet().stream()
                .map(e -> new MonthlyReward(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        int totalPoints = monthlyRewards.stream().mapToInt(MonthlyReward::getPoints).sum();

        return new CustomerRewardSummary(customerId, customerName, monthlyRewards, totalPoints);
    }
}
