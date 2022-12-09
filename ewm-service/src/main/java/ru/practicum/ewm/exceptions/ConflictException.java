package ru.practicum.ewm.exceptions;

import lombok.Getter;

@Getter
public class ConflictException extends Exception {
    private String reason;

    public ConflictException(String message, String reason) {
        super(message);
        this.reason = reason;
    }
}
