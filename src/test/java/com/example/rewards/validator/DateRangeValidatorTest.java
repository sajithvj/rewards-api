package com.example.rewards.validator;

import com.example.rewards.exception.DateRangeException;
import com.example.rewards.validation.DateRange;
import com.example.rewards.validation.DateRangeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class DateRangeValidatorTest {
    @Autowired
    private DateRangeValidator dateRangeValidator;

    @BeforeEach
    public void setup() {
        dateRangeValidator = new DateRangeValidator();
    }

    @Test
    public void validate_Date() {
        String startDate = "2026-06-09";
        String endDate = "2026-08-09";
        DateRange dateRange = dateRangeValidator.validateDateRange(startDate, endDate);
        assertEquals("2026-06-09", dateRange.startDate().toString());
        assertEquals("2026-08-09", dateRange.endDate().toString());
    }

    @Test
    public void throwsExceptionWhenEndDateIsNull() {
        String startDate = "2026-06-09";
        assertThrows(DateRangeException.class, () -> dateRangeValidator.validateDateRange(startDate, null));
    }

    @Test
    public void throwsExceptionWhenStartDateIsNull() {
        String endDate = "2026-06-09";
        assertThrows(DateRangeException.class, () -> dateRangeValidator.validateDateRange(null, endDate));
    }

    @Test
    public void validateWhenBothDateAreNull() {
        LocalDate startDate = LocalDate.now().minusMonths(3);
        LocalDate endDate = LocalDate.now();
        DateRange dateRange = dateRangeValidator.validateDateRange(null, null);
        assertEquals(startDate.toString(), dateRange.startDate().toString());
        assertEquals(endDate.toString(), dateRange.endDate().toString());

    }


}
