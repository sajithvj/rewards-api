package com.example.rewards.validation;

import com.example.rewards.exception.DateRangeException;
import com.example.rewards.util.DateParameterParser;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DateRangeValidator {
    public DateRange validateDateRange(String startDateStr, String endDateStr) {
        LocalDate startDate;
        LocalDate endDate;
        if ((startDateStr == null || startDateStr.isEmpty()) && (endDateStr == null || endDateStr.isEmpty())) {
            startDate = LocalDate.now().minusMonths(3);
            endDate = LocalDate.now();
        } else if ((startDateStr == null || startDateStr.isEmpty()) != (endDateStr == null || endDateStr.isEmpty())) {
            throw new DateRangeException("Both start date and end date must be provided together or both must be null.");
        } else {
            startDate = DateParameterParser.parse("startDate", startDateStr);
            endDate = DateParameterParser.parse("endDate", endDateStr);
            // 3. Chronological order check
            if (startDate.isAfter(endDate)) {

                throw new DateRangeException("Start date must be before or equal to end date.");
            }
            // 4. One-year historical limit check (cache LocalDate.now() execution)
            LocalDate minAllowedStartDate = LocalDate.now().minusYears(1);
            if (startDate.isBefore(minAllowedStartDate)) {

                throw new DateRangeException("Start date cannot be more than one year in the past.");
            }

            // 5. Maximum range window check (exact 3-month bound evaluation)
            if (startDate.plusMonths(3).isBefore(endDate)) {

                throw new DateRangeException("Date range cannot exceed three months.");
            }
        }
        return new DateRange(startDate, endDate);
    }
}
