package ru.practicum.ewm.exceptions;

import lombok.Getter;

@Getter
public class EventNotFoundException extends Exception {
    private String reason;

    public EventNotFoundException(String message, String reason) {
        super(message);
        this.reason = reason;
    }
}
