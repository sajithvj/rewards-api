package com.example.rewards.service;

import com.example.rewards.dto.CustomerRewardSummary;
import com.example.rewards.dto.MonthlyReward;
import com.example.rewards.exception.AppException;
import com.example.rewards.exception.CustomerNotFoundException;
import com.example.rewards.model.Transaction;
import com.example.rewards.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class RewardService {
    private static final Logger log = LoggerFactory.getLogger(RewardService.class);
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

            BigDecimal fiftyToHundred =  remaining.subtract(TIER_1_THRESHOLD); // 1 point per dollar over $50 up to $100
            points = points.add(fiftyToHundred);
        }

        return points.intValue();
    }

    /**
     * Builds a per-customer summary of reward points, broken down by
     * calendar month, plus the total across all months on record.
     */

    public List<CustomerRewardSummary> getRewardSummaries(LocalDate startDate, LocalDate endDate) {

        Map<String, List<Transaction>> byCustomer = transactionRepository.findByTransactionDateBetween(startDate, endDate).stream()
                .collect(Collectors.groupingBy(Transaction::customerId));
        List<CustomerRewardSummary> summaries = byCustomer.entrySet().stream()
                .map(entry -> buildSummary(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(CustomerRewardSummary::customerName))
                .collect(Collectors.toList());
        if (summaries.isEmpty()) {
            log.warn("No transactions found between {} and {}", startDate, endDate);
            throw new AppException("No transactions found in the specified date range.", HttpStatus.NOT_FOUND);
        }
        return summaries;
    }

    /**
     * Builds  summary of reward points per customer, broken down by
     * calendar month, plus the total across all months on record.
     */
    public CustomerRewardSummary getRewardSummaryByCustomerId(String customerId, Integer months) {
        if(months == null || months <= 0|| customerId == null || customerId.isEmpty()) {
            log.error("Invalid input parameters: customerId={}, months={}", customerId, months);
            throw new IllegalArgumentException("Invalid input parameters: customerId and months must be provided and valid.");
        }
        LocalDate startDate = LocalDate.now().minusMonths(months);
        LocalDate endDate = LocalDate.now();
        List<Transaction> customerTransactions = transactionRepository.findByCustomerIdAndTransactionDateBetween(customerId, startDate, endDate);

        if (customerTransactions.isEmpty()) {
            throw new CustomerNotFoundException(customerId); // or throw an exception, depending on your design choice
        }

        return buildSummary(customerId, customerTransactions);
    }

    private CustomerRewardSummary buildSummary(String customerId, List<Transaction> customerTransactions) {
        String customerName = customerTransactions.get(0).customerName();

        // TreeMap keeps months in chronological order in the response.
        Map<YearMonth, Integer> pointsByMonth = new TreeMap<>();
        Map<YearMonth, List<String>> transactionByMonth = new TreeMap<>();
        for (Transaction t : customerTransactions) {
            YearMonth month = YearMonth.from(t.transactionDate());
            int points = calculatePoints(t.amount());
            pointsByMonth.merge(month, points, Integer::sum);
            transactionByMonth.computeIfAbsent(month, k -> new java.util.ArrayList<>()).add(t.transactionId());
        }


        List<MonthlyReward> monthlyRewards = pointsByMonth.entrySet().stream()
                .map(e -> new MonthlyReward(e.getKey().getYear(),e.getKey().getMonth().name(), e.getValue(), transactionByMonth.get(e.getKey())))
                .collect(Collectors.toList());

        int totalPoints = monthlyRewards.stream().mapToInt(MonthlyReward::points).sum();

        return new CustomerRewardSummary(customerId, customerName, monthlyRewards, totalPoints);
    }
}
