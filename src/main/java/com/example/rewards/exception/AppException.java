package com.example.rewards.exception;

import org.springframework.http.HttpStatus;

public class AppException extends RuntimeException {
    private final HttpStatus errCode;

    public AppException(String message, HttpStatus errCode) {
        super(message);
        this.errCode = errCode;
    }

    public AppException(String message, Throwable cause, HttpStatus errCode) {
        super(message, cause);
        this.errCode = errCode;
    }

    public HttpStatus getErrCode() {
        return errCode;
    }
}
