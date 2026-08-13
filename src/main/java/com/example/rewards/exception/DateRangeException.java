package com.example.rewards.exception;

public class DateRangeException extends RuntimeException {

    public DateRangeException(String message) {
        super(message);
    }

    public DateRangeException(String message, Throwable cause) {
        super(message, cause);
    }


}
