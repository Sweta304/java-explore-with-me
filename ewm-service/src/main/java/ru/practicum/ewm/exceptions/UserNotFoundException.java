package ru.practicum.ewm.exceptions;

import lombok.Getter;

@Getter
public class UserNotFoundException extends Exception {
    private String reason;

    public UserNotFoundException(String message, String reason) {
        super(message);
        this.reason = reason;
    }
}
