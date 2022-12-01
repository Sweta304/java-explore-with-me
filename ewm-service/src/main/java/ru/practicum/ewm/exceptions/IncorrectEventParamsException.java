package ru.practicum.ewm.exceptions;

import lombok.Getter;

@Getter
public class IncorrectEventParamsException extends Exception {
    private String reason;

    public IncorrectEventParamsException(String message, String reason) {
        super(message);
        this.reason = reason;
    }
}
