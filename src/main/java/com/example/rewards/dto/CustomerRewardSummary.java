package com.example.rewards.dto;

import java.util.List;

/**
 * A customer's reward points broken down by month, plus the running total
 * across the whole reporting period.
 */
public class CustomerRewardSummary {

    private final String customerId;
    private final String customerName;
    private final List<MonthlyReward> monthlyRewards;
    private final int totalPoints;

    public CustomerRewardSummary(String customerId, String customerName,
                                  List<MonthlyReward> monthlyRewards, int totalPoints) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.monthlyRewards = monthlyRewards;
        this.totalPoints = totalPoints;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<MonthlyReward> getMonthlyRewards() {
        return monthlyRewards;
    }

    public int getTotalPoints() {
        return totalPoints;
    }
}
