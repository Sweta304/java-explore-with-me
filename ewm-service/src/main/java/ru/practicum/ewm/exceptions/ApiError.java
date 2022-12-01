package ru.practicum.ewm.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static utils.Constants.DATE_TIME_FORMATTER;

public class ApiError {
    String errors;
    String message;
    String reason;
    String status;
    String timestamp;

    public ApiError(String errors, String message, String reason, HttpStatus status) {
        this.errors = errors;
        this.message = message;
        this.reason = reason;
        this.status = status.toString();
        this.timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }
}
