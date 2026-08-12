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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
            BigDecimal applicableAmount = remaining.min(TIER_2_THRESHOLD);
            BigDecimal fiftyToHundred = applicableAmount.subtract(TIER_1_THRESHOLD); // 1 point per dollar over $50 up to $100
            points = points.add(fiftyToHundred.setScale(0, RoundingMode.HALF_UP)); // Truncate fractional dollars
        }

        return points.intValue();
    }

    /**
     * Builds a per-customer summary of reward points, broken down by
     * calendar month, plus the total across all months on record.
     */

    public List<CustomerRewardSummary> getRewardSummaries(LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        Map<String, List<Transaction>> byCustomer = transactionRepository.findByTransactionDateBetween(startDate, endDate).stream()
                .collect(Collectors.groupingBy(Transaction::customerId));
        List<CustomerRewardSummary> summaries = byCustomer.entrySet().stream()
                .map(entry -> buildSummary(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(CustomerRewardSummary::customerName))
                .collect(Collectors.toList());
        if (summaries.isEmpty()) {
            log.warn("No transactions found between {} and {}", startDate, endDate);
            throw new CustomerNotFoundException(startDate, endDate);
        }
        return summaries;
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
                .map(e -> new MonthlyReward(e.getKey().getYear(), e.getKey().getMonth().name(), e.getValue(), transactionByMonth.get(e.getKey())))
                .collect(Collectors.toList());

        int totalPoints = monthlyRewards.stream().mapToInt(MonthlyReward::points).sum();

        return new CustomerRewardSummary(customerId, customerName, monthlyRewards, totalPoints);
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        // 1. Check for mutual presence/absence
        if ((startDate == null) != (endDate == null)) {
            log.error("Exclusive null dates provided: startDate={}, endDate={}", startDate, endDate);
            throw new AppException("Both start date and end date must be provided together or both must be null.", HttpStatus.BAD_REQUEST);
        }

        // 2. Early exit if both are null (valid state)
        if (startDate == null && endDate == null) {
            log.error("Both startDate and endDate are null, which is not allowed.");
            throw new AppException("Both start date and end date cannot be null.", HttpStatus.BAD_REQUEST);
        }

        // 3. Chronological order check
        if (startDate.isAfter(endDate)) {
            log.error("Invalid sequence: startDate {} is after endDate {}", startDate, endDate);
            throw new AppException("Start date must be before or equal to end date.", HttpStatus.BAD_REQUEST);
        }


        // 4. One-year historical limit check (cache LocalDate.now() execution)
        LocalDate minAllowedStartDate = LocalDate.now().minusYears(1);
        if (startDate.isBefore(minAllowedStartDate)) {
            log.error("Start date {} exceeds 1-year historical limit.", startDate);
            throw new AppException("Start date cannot be more than one year in the past.", HttpStatus.BAD_REQUEST);
        }

        // 5. Maximum range window check (exact 3-month bound evaluation)
        if (startDate.plusMonths(3).isBefore(endDate)) {
            log.error("Date span exceeds 3 months: startDate={}, endDate={}", startDate, endDate);
            throw new AppException("Date range cannot exceed three months.", HttpStatus.BAD_REQUEST);
        }

    }

}
