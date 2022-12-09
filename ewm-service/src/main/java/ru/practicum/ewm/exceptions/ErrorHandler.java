package ru.practicum.ewm.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(final ValidationException e) {
        log.info("400 {}", e.getMessage(), e);
        return new ApiError(getStackTrace(e), e.getMessage(), e.getReason(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handle(final UserNotFoundException e) {
        log.info("404 {}", e.getMessage(), e);
        return new ApiError(getStackTrace(e), e.getMessage(), e.getReason(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handle(final UserAlreadyExistsException e) {
        log.info("409 {}", e.getMessage(), e);
        return new ApiError(getStackTrace(e), e.getMessage(), e.getCause().getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(final EmailException e) {
        log.info("400 {}", e.getMessage(), e);
        return new ApiError(getStackTrace(e), e.getMessage(), e.getCause().getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(final MethodArgumentNotValidException e) {
        log.info("400 {}", e.getMessage(), e);
        return new ApiError(getStackTrace(e), e.getMessage(), e.getCause().getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(final IllegalArgumentException e) {
        log.info("400 {}", e.getMessage(), e);
        return new ApiError(getStackTrace(e), e.getMessage(), e.getCause().getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(final IncorrectEventStateException e) {
        log.info("400 {}", e.getMessage(), e);
        return new ApiError(getStackTrace(e), e.getMessage(), e.getCause().getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handle(final EventNotFoundException e) {
        log.info("404 {}", e.getMessage(), e);
        return new ApiError(getStackTrace(e), e.getMessage(), e.getReason(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handle(final IncorrectEventParamsException e) {
        log.info("403 {}", e.getMessage(), e);
        return new ApiError(getStackTrace(e), e.getMessage(), e.getReason(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handle(final CategoryNotFoundException e) {
        log.info("404 {}", e.getMessage(), e);
        return new ApiError(getStackTrace(e), e.getMessage(), e.getReason(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handle(final CompilationNotFoundException e) {
        log.info("404 {}", e.getMessage(), e);
        return new ApiError(getStackTrace(e), e.getMessage(), e.getReason(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handle(final ConflictException e) {
        log.info("409 {}", e.getMessage(), e);
        return new ApiError(getStackTrace(e), e.getMessage(), e.getReason(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handle(final ForbiddenException e) {
        log.info("403 {}", e.getMessage(), e);
        return new ApiError(getStackTrace(e), e.getMessage(), e.getReason(), HttpStatus.FORBIDDEN);
    }

    private String getStackTrace(Exception e) {
        Writer writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
