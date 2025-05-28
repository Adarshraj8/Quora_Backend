package com.quora.quora_backend.Exception;


public class AppException extends RuntimeException {
    public AppException(String message) {
        super(message);
    }
}
