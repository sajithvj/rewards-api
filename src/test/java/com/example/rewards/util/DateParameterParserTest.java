package com.example.rewards.util;

import com.example.rewards.exception.AppException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class DateParameterParserTest {


    @Test
    void parse_ValidIsoDate_ReturnsLocalDate() {
        LocalDate result = DateParameterParser.parse("startDate", "2026-05-15");
        assertEquals(LocalDate.of(2026, 5, 15), result);
    }

    @Test
    void parse_ValidIsoDateWithWhitespace_TrimsAndParses() {
        LocalDate result = DateParameterParser.parse("endDate", "  2026-12-31  ");
        assertEquals(LocalDate.of(2026, 12, 31), result);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void parse_NullOrBlank_ReturnsNull(String input) {
        assertNull(DateParameterParser.parse("anyParam", input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"15-05-2026", "2026/05/15", "2026-05-32", "abc", "2026-5-15"})
    void parse_InvalidFormats_ThrowsAppException(String invalidInput) {
        AppException exception = assertThrows(AppException.class, () ->
                DateParameterParser.parse("testParam", invalidInput)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getErrCode());
        assertTrue(exception.getMessage().contains("Invalid date format for parameter 'testParam'"));
        assertTrue(exception.getMessage().contains(invalidInput));
        assertNotNull(exception.getCause());
    }


}
