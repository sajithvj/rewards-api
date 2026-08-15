package com.example.rewards.dto;

import java.util.List;

/**
 * Reward points earned by a customer during a single calendar month.
 */
public record MonthlyReward(int year, String month, int points, List<MonthlyTransaction> transactionIds) {


    public MonthlyReward(int year, String month, int points, List<MonthlyTransaction> transactionIds) {

        this.month = month;
        this.year = year;
        this.points = points;
        this.transactionIds = transactionIds;
    }


}
