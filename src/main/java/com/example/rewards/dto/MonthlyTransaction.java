package com.example.rewards.dto;

import java.math.BigDecimal;

public record MonthlyTransaction(String transactionId, BigDecimal amount) {
    public MonthlyTransaction(String transactionId,BigDecimal amount){
        this.transactionId=transactionId;
        this.amount=amount;
    }
}
