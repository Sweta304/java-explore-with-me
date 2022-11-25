package ru.practicum.ewm.user;

import lombok.Getter;

@Getter
public class ValidationException extends Exception {
    private String reason;

    public ValidationException(String message, String reason) {
        super(message);
        this.reason = reason;
    }

}
