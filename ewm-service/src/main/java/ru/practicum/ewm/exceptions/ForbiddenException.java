package ru.practicum.ewm.exceptions;

import lombok.Getter;

@Getter
public class ForbiddenException extends Exception {
    private String reason;

    public ForbiddenException(String message, String reason) {
        super(message);
        this.reason = reason;
    }
}
