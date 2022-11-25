package utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.controllers.admin.AdminUserController;
import ru.practicum.ewm.exceptions.ApiError;
import ru.practicum.ewm.user.EmailException;
import ru.practicum.ewm.user.UserAlreadyExistsException;
import ru.practicum.ewm.user.UserNotFoundException;
import ru.practicum.ewm.user.ValidationException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

@Slf4j
@RestControllerAdvice(assignableTypes = {AdminUserController.class})
public class ErrorHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(final ValidationException e) {
        log.info("400 {}", e.getMessage(), e);
        return new ApiError(getStackTrace(e), e.getMessage(), e.getReason(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handle(final UserNotFoundException e) {
        log.info("404 {}", e.getMessage(), e);
        return new ApiError(getStackTrace(e), e.getMessage(), e.getReason(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handle(final Exception e) {
        log.info("500 {}", e.getMessage(), e);
        return new ApiError(getStackTrace(e), e.getMessage(), e.getCause().getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handle(final UserAlreadyExistsException e) {
        log.info("409 {}", e.getMessage(), e);
        return new ApiError(getStackTrace(e), e.getMessage(), e.getCause().getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(final EmailException e) {
        log.info("400 {}", e.getMessage(), e);
        return new ApiError(getStackTrace(e), e.getMessage(), e.getCause().getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(final MethodArgumentNotValidException e) {
        log.info("400 {}", e.getMessage(), e);
        return new ApiError(getStackTrace(e), e.getMessage(), e.getCause().getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    private String getStackTrace(Exception e) {
        Writer writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
