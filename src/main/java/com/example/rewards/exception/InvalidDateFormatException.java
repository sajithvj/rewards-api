package com.example.rewards.exception;

/**
 * Thrown when a caller-supplied date string doesn't match the expected
 * ISO format (yyyy-MM-dd).
 */
public class InvalidDateFormatException extends RuntimeException {

    public InvalidDateFormatException(String paramName, String value) {
        super("Invalid value for parameter '" + paramName + "': '" + value
                + "' - expected format yyyy-MM-dd");
    }
}
