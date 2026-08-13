package com.example.rewards.service;

import com.example.rewards.dto.CustomerRewardSummary;
import com.example.rewards.dto.MonthlyReward;
import com.example.rewards.exception.CustomerNotFoundException;
import com.example.rewards.model.Transaction;
import com.example.rewards.repository.TransactionRepository;
import com.example.rewards.validation.DateRange;
import com.example.rewards.validation.DateRangeValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RewardService {
    private static final Logger log = LoggerFactory.getLogger(RewardService.class);
    private static final BigDecimal TIER_2_THRESHOLD = new BigDecimal("100");
    private static final BigDecimal TIER_1_THRESHOLD = new BigDecimal("50");

    private final TransactionRepository transactionRepository;
    private final DateRangeValidator dateRangeValidator;

    public RewardService(TransactionRepository transactionRepository,DateRangeValidator dateRangeValidator) {
        this.transactionRepository = transactionRepository;
        this.dateRangeValidator=dateRangeValidator;
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
            points = points.add(overHundred.setScale(0,RoundingMode.FLOOR).multiply(BigDecimal.valueOf(2)));
            remaining = TIER_2_THRESHOLD;
        }

        if (remaining.compareTo(TIER_1_THRESHOLD) > 0) {
            BigDecimal applicableAmount = remaining.min(TIER_2_THRESHOLD);
            BigDecimal fiftyToHundred = applicableAmount.subtract(TIER_1_THRESHOLD); // 1 point per dollar over $50 up to $100
            points = points.add(fiftyToHundred.setScale(0, RoundingMode.FLOOR)); // Truncate fractional dollars
        }

        return points.intValue();
    }

    /**
     * Builds a per-customer summary of reward points, broken down by
     * calendar month, plus the total across all months on record.
     */

    public List<CustomerRewardSummary> getRewardSummaries(String startDateStr, String endDateStr) {

        DateRange dateRange = dateRangeValidator.validateDateRange(startDateStr,endDateStr);
        Map<String, List<Transaction>> byCustomer = transactionRepository.findByTransactionDateBetween(dateRange.startDate(), dateRange.endDate()).stream()
                .collect(Collectors.groupingBy(Transaction::customerId));
        List<CustomerRewardSummary> summaries = byCustomer.entrySet().stream()
                .map(entry -> buildSummary(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(CustomerRewardSummary::customerName))
                .collect(Collectors.toList());
        if (summaries.isEmpty()) {
            log.warn("No transactions found between {} and {}", dateRange.startDate(), dateRange.endDate());
            throw new CustomerNotFoundException(dateRange.startDate(), dateRange.endDate());
        }
        return summaries;
    }


    private CustomerRewardSummary buildSummary(String customerId, List<Transaction> customerTransactions) {
        String customerName = customerTransactions.stream().findFirst().orElseThrow(NullPointerException::new).customerName();

        // TreeMap keeps months in chronological order in the response.
        Map<YearMonth, Integer> pointsByMonth = new TreeMap<>();
        Map<YearMonth, List<String>> transactionByMonth = new TreeMap<>();
        customerTransactions.forEach(t->{
            YearMonth month=YearMonth.from(t.transactionDate());
            int points =calculatePoints(t.amount());
            pointsByMonth.merge(month,points,Integer::sum);
            transactionByMonth.computeIfAbsent(month,k-> new ArrayList<>()).add(t.transactionId());
        });



        List<MonthlyReward> monthlyRewards = pointsByMonth.entrySet().stream()
                .map(e -> new MonthlyReward(e.getKey().getYear(), e.getKey().getMonth().name(), e.getValue(), transactionByMonth.get(e.getKey())))
                .toList();

        int totalPoints = monthlyRewards.stream().mapToInt(MonthlyReward::points).sum();

        return new CustomerRewardSummary(customerId, customerName, monthlyRewards, totalPoints);
    }


}
