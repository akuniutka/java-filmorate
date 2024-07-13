package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    protected ProblemDetail handleNotFoundException(NotFoundException exception) {
        String detail = "Check that id of %s is correct (you sent %s)".formatted(exception.getModelName(),
                exception.getModelId());
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, detail);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
            @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        HttpStatus statusCode = HttpStatus.BAD_REQUEST;
        String detail = exception.getBindingResult().getFieldErrors().stream()
                .map(e -> "'%s' %s".formatted(e.getField(), e.getDefaultMessage()))
                .collect(Collectors.joining("; "));

        return handleExceptionInternal(exception, ProblemDetail.forStatusAndDetail(statusCode, detail), headers,
                statusCode, request);
    }
}
