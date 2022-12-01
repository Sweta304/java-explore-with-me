package ru.practicum.ewm.exceptions;

import lombok.Getter;

@Getter
public class CompilationNotFoundException extends Exception {
    private String reason;

    public CompilationNotFoundException(String message, String reason) {
        super(message);
        this.reason = reason;
    }
}
