package ru.practicum.ewm.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ApiError {
    String errors;
    String message;
    String reason;
    String status;
    String timestamp;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ApiError(String errors, String message, String reason, HttpStatus status) {
        this.errors = errors;
        this.message = message;
        this.reason = reason;
        this.status = status.toString();
        this.timestamp = LocalDateTime.now().format(dateTimeFormatter);
    }
}
