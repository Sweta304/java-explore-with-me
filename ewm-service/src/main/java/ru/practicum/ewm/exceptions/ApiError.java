package ru.practicum.ewm.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static ru.practicum.ewm.utils.Constants.DATE_TIME_FORMATTER;

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


    public String getErrors() {
        return errors;
    }

    public String getMessage() {
        return message;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
