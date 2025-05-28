package com.quora.quora_backend.Dto;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String message;
    private LocalDateTime timestamp;

    public ErrorResponse(String message, LocalDateTime timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getters
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
}