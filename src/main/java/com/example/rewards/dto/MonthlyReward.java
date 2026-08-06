package com.example.rewards.dto;

import java.time.YearMonth;

/**
 * Reward points earned by a customer during a single calendar month.
 */
public class MonthlyReward {

    private final YearMonth month;
    private final int points;

    public MonthlyReward(YearMonth month, int points) {
        this.month = month;
        this.points = points;
    }

    public YearMonth getMonth() {
        return month;
    }

    public int getPoints() {
        return points;
    }
}
