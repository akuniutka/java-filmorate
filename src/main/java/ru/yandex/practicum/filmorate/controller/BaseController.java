package ru.yandex.practicum.filmorate.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public abstract class BaseController {

    protected void logRequest(final HttpServletRequest request) {
        final String queryString = translateQueryString(request);
        log.info("Received {} at {}{}", request.getMethod(), request.getRequestURI(), queryString);
    }

    protected void logRequest(final HttpServletRequest request, final Object body) {
        final String queryString = translateQueryString(request);
        log.info("Received {} at {}{}: {}", request.getMethod(), request.getRequestURI(), queryString, body);
    }

    protected void logResponse(final HttpServletRequest request) {
        final String queryString = translateQueryString(request);
        log.info("Responded to {} {}{} with no body", request.getMethod(), request.getRequestURI(), queryString);

    }

    protected void logResponse(final HttpServletRequest request, final Object body) {
        final String queryString = translateQueryString(request);
        log.info("Responded to {} {}{}: {}", request.getMethod(), request.getRequestURI(), queryString, body);

    }

    protected String translateQueryString(final HttpServletRequest request) {
        return Optional.ofNullable(request.getQueryString()).map(s -> "?" + s).orElse("");
    }
}
