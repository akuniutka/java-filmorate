package ru.yandex.practicum.filmorate.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;
import ru.yandex.practicum.filmorate.util.CachedHttpServletRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        CachedHttpServletRequest cachedRequest = new CachedHttpServletRequest(request);
        ContentCachingResponseWrapper cachedResponse;
        if (response instanceof ContentCachingResponseWrapper wrapper) {
            cachedResponse = wrapper;
        } else {
            cachedResponse = new ContentCachingResponseWrapper(response);
        }
        try {
            if (log.isInfoEnabled()) {
                String requestBody = cachedRequest.getBody();
                if (requestBody.isEmpty()) {
                    log.info("Received {} at {} with no body", cachedRequest.getMethod(),
                            cachedRequest.getRequestURI());
                } else {
                    log.info("Received {} at {}: {}", cachedRequest.getMethod(), cachedRequest.getRequestURI(),
                            requestBody);
                }
            }
            filterChain.doFilter(cachedRequest, cachedResponse);
        } finally {
            if (log.isInfoEnabled()) {
                String responseBody = new String(cachedResponse.getContentAsByteArray(), StandardCharsets.UTF_8);
                if (responseBody.isEmpty()) {
                    log.info("Responded to {} at {} with {} and no body", cachedRequest.getMethod(),
                            cachedRequest.getRequestURI(), cachedResponse.getStatus());
                } else {
                    log.info("Responded to {} at {} with {}: {}", cachedRequest.getMethod(),
                            cachedRequest.getRequestURI(),
                            cachedResponse.getStatus(), responseBody);
                }
            }
            cachedResponse.copyBodyToResponse();
        }
    }
}
