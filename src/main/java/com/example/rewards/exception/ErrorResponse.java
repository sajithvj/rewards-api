package com.example.rewards.exception;

import java.time.LocalDateTime;

public record ErrorResponse(String details, int statusCode, String path, LocalDateTime timestamp) {

}
