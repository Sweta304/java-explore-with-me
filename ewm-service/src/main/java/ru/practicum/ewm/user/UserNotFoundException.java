package ru.practicum.ewm.user;

import lombok.Getter;

@Getter
public class UserNotFoundException extends Exception {
    private String reason;

    public UserNotFoundException(String message, String reason) {
        super(message);
        this.reason = reason;
    }
}
