package com.example.rewards.util;

import com.example.rewards.exception.AppException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DateParameterParser {

    private DateParameterParser() {
        // Private constructor to prevent instantiation
    }

    /**
     * @param paramName name of the request parameter, used in the error message if parsing fails
     * @param rawValue  the raw string value, or {@code null}/blank if the parameter was omitted
     * @return the parsed date, or {@code null} if rawValue was not supplied
     * @throws AppException if rawValue is present but not a valid yyyy-MM-dd date
     */
    public static LocalDate parse(String paramName, String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(rawValue.trim());
        } catch (DateTimeParseException e) {
            throw new AppException("Invalid date format for parameter '" + paramName + "': expected yyyy-MM-dd, got '" + rawValue + "'", e, HttpStatus.BAD_REQUEST);
        }
    }

}
