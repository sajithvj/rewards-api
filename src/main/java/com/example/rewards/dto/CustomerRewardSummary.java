package com.example.rewards.dto;

import java.util.List;

/**
 * A customer's reward points broken down by month, plus the running total
 * across the whole reporting period.
 */
public record CustomerRewardSummary(String customerId, String customerName, List<MonthlyReward> monthlyRewards,
                                    int totalPoints) {

    public CustomerRewardSummary(String customerId, String customerName,
                                 List<MonthlyReward> monthlyRewards, int totalPoints) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.monthlyRewards = monthlyRewards;
        this.totalPoints = totalPoints;
    }


}
