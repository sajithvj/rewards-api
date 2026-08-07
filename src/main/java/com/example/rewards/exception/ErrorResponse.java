package com.example.rewards.exception;

import java.time.LocalDateTime;

public record ErrorResponse(String details, int statusCode, LocalDateTime timestamp) {

    public ErrorResponse(String details, int statusCode) {
        this(details, statusCode, LocalDateTime.now());
    }
}
