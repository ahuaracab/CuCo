package com.cuco.exception;

public class ApiException extends RuntimeException {
    private final String errorType;

    public ApiException(String message, String errorType) {
        super(message);
        this.errorType = errorType;
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.errorType = null;
    }

    public String getErrorType() {
        return errorType;
    }
}
