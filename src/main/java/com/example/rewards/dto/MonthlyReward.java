package com.example.rewards.dto;

import java.time.YearMonth;
import java.util.List;

/**
 * Reward points earned by a customer during a single calendar month.
 */
public class MonthlyReward {

    private final YearMonth month;
    private final int points;
    List<String> transactionIds;

    public MonthlyReward(YearMonth month, int points, List<String> transactionIds) {

        this.month = month;
        this.points = points;
        this.transactionIds = transactionIds;
    }

    public YearMonth getMonth() {
        return month;
    }

    public int getPoints() {
        return points;
    }

    public List<String> getTransactionIds() {
        return transactionIds;
    }
}
