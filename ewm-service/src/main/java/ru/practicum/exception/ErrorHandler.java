package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.exception.model.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException ex) {
        log.warn(ex.getMessage());

        return new ErrorResponse(
                HttpStatus.NOT_FOUND,
                "The required object was not found.",
                ex.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn(ex.getMessage());

        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> "Filed: " + e.getField() + ". Error: " + e.getDefaultMessage() + ". Value: " + e.getRejectedValue())
                .collect(Collectors.joining("; "));

        return new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                message,
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentTypeMismatchException ex) {
        log.warn(ex.getMessage());

        return new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                ex.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.warn(ex.getMessage());

        return new ErrorResponse(
                HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                ex.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(BadRequestException ex) {
        log.warn(ex.getMessage());

        return new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                ex.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenException(ForbiddenException ex) {
        log.warn(ex.getMessage());

        return new ErrorResponse(
                HttpStatus.FORBIDDEN,
                "For the requested operation the conditions are not met.",
                ex.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public ErrorResponse handleNoChangesRequired(NoChangesRequired ex) {
        log.warn(ex.getMessage());

        return new ErrorResponse(
                HttpStatus.OK,
                null,
                ex.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(ConflictException ex) {
        log.warn(ex.getMessage());

        return new ErrorResponse(
                HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                ex.getMessage(),
                LocalDateTime.now()
        );
    }
}
