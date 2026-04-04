package com.studentid.identity_system;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}