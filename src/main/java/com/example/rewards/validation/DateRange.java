package com.example.rewards.validation;

import java.time.LocalDate;

public record DateRange(LocalDate startDate,LocalDate endDate) {
   public DateRange(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
