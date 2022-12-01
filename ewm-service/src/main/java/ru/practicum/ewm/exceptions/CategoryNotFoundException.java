package ru.practicum.ewm.exceptions;

import lombok.Getter;

@Getter
public class CategoryNotFoundException extends Exception {
    private String reason;

    public CategoryNotFoundException(String message, String reason) {
        super(message);
        this.reason = reason;
    }
}
